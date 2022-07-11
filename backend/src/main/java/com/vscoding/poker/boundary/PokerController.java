package com.vscoding.poker.boundary;

import com.vscoding.poker.boundary.bean.SessionCreationResponse;
import com.vscoding.poker.boundary.bean.UserCreationRequest;
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

  @MessageMapping("/join/{sessionId}/{personalToken}")
  @SendTo("/topic/session/{sessionId}")
  public VotingSessionResponse joinSession(
          @DestinationVariable String sessionId,
          @DestinationVariable String personalToken,
          @Payload UserCreationRequest user) {
    log.info("Receiving new user creation '{}'", user.getUsername());

    // Create new user in the session
    var newVote = service.createNewUser(user.getUsername(), sessionId);

    // send user his voting id back, by using his personal feed
    var userCreationResponse = ModelMapper.toUserCreationResponse(newVote);
    smt.convertAndSend("/topic/personal/" + personalToken, userCreationResponse);

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
  public SessionCreationResponse createSession(@DestinationVariable String personalToken) {
    log.info("Receiving create session request");

    return service.createNewSession(personalToken);
  }
}
