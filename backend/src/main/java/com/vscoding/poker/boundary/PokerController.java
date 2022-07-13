package com.vscoding.poker.boundary;

import com.vscoding.poker.boundary.bean.SessionCreationResponse;
import com.vscoding.poker.boundary.bean.UserRequest;
import com.vscoding.poker.boundary.bean.UserResponse;
import com.vscoding.poker.boundary.bean.VotingSessionResponse;
import com.vscoding.poker.control.ModelMapper;
import com.vscoding.poker.control.PlanningPokerService;
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
          @Payload UserRequest user) {
    log.info("Receiving new user creation '{}'", user.getUsername());

    // Create new user in the session
    var newUser = service.joinSession(user, sessionId);

    if (!user.getPersonalToken().equals(newUser.getId())) {
      // sender does not have a user and has used a temporary ID, so we send him a new userId to his
      // personal feed
      var userCreationResponse = ModelMapper.toUserResponse(newUser);
      smt.convertAndSend("/topic/personal/" + user.getPersonalToken(), userCreationResponse);
    }

    // Notify all users about the new participant
    return ModelMapper.toVotingSessionResponse(service.getSession(sessionId));
  }

  @MessageMapping("/vote/{sessionId}/{voteId}")
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
  @SendTo("/topic/personal/{personalToken}")
  public SessionCreationResponse createSession(@Payload UserRequest request) {
    log.info("Receiving create session request");

    return service.createNewSession(request);
  }

  @MessageMapping("/validateUser/{personalToken}")
  @SendTo("/topic/personal/{personalToken}")
  public UserResponse validateUser(@Payload UserRequest request) {
    log.info("Receiving user validation request");

    var user = service.getOrCreateUser(request.getPersonalToken(), request.getUsername());

    return ModelMapper.toUserResponse(user);
  }
}
