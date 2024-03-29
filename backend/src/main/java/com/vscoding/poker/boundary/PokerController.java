package com.vscoding.poker.boundary;

import com.vscoding.poker.boundary.bean.BaseMassageResponse;
import com.vscoding.poker.boundary.bean.SessionCreationResponse;
import com.vscoding.poker.boundary.bean.UserRequest;
import com.vscoding.poker.boundary.bean.UserResponse;
import com.vscoding.poker.boundary.bean.VotingSessionResponse;
import com.vscoding.poker.control.ModelMapper;
import com.vscoding.poker.control.PlanningPokerService;
import com.vscoding.poker.entity.UserModel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;


@Slf4j
@Controller
@AllArgsConstructor
public class PokerController {

  private final PlanningPokerService service;
  private final SimpMessagingTemplate smt;

  @MessageMapping("/join/{sessionId}")
  @SendTo("/topic/session/{sessionId}")
  public VotingSessionResponse joinSession(
          @DestinationVariable String sessionId,
          @Payload UserRequest request) {
    log.info("Receiving new user join '{}'", request.getUsername());

    // Create new user in the session
    var user = service.joinSession(request, sessionId);

    notifyUserAboutChangedId(request, user);

    // Notify all users about the new participant
    return ModelMapper.toVotingSessionResponse(service.getSession(sessionId));
  }

  @MessageMapping("/vote/{sessionId}/{userId}")
  @SendTo("/topic/session/{sessionId}")
  public VotingSessionResponse vote(
          @DestinationVariable String userId,
          @DestinationVariable String sessionId,
          @Payload String vote) {
    log.info("Receiving vote {} with content: {}", userId, vote);

    service.addVote(userId, vote, sessionId);

    // Notify all about the vote
    return ModelMapper.toVotingSessionResponse(service.getSession(sessionId));
  }

  @MessageMapping("/createSession/{personalToken}")
  public void createSession(@Payload UserRequest request) {
    log.info("Receiving create session request from {}", request);
    var user = service.getOrCreateUser(request.getPersonalToken(), request.getUsername());

    notifyUserAboutChangedId(request, user);

    var sessionResponse = service.createNewSession(user);

    // send the creation to the old feed, this is necessary if the user takes longer to subscribe to
    // the old feed
    sendToPersonalFeed(request.getPersonalToken(), sessionResponse);

    if (!user.getId().equals(request.getPersonalToken())) {
      // the user was created, so it can expect it to subscribe to the new feed
      sendToPersonalFeed(user.getId(), sessionResponse);
    }
  }

  @MessageMapping("/validateUser/{personalToken}")
  @SendTo("/topic/personal/{personalToken}")
  public UserResponse validateUser(@Payload UserRequest request) {
    log.info("Receiving user validation request");

    var user = service.getOrCreateUser(request.getPersonalToken(), request.getUsername());

    return ModelMapper.toUserResponse(user);
  }

  /**
   * If the user was using a temporary token we need to notify the user about the change of the
   * personal token.
   *
   * @param request {@link UserRequest} user request
   * @param user    {@link UserModel} user in DB
   */
  private void notifyUserAboutChangedId(UserRequest request, UserModel user) {
    if (!request.getPersonalToken().equals(user.getId())) {
      // sender does not have a user and has used a temporary ID, so we send him a new userId to his
      // personal feed
      var userCreationResponse = ModelMapper.toUserResponse(user);
      sendToPersonalFeed(request.getPersonalToken(), userCreationResponse);
    }
  }

  private void sendToPersonalFeed(String id, BaseMassageResponse response) {
    smt.convertAndSend("/topic/personal/" + id, response);
  }
}
