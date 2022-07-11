package com.vscoding.poker.boundary;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.vscoding.poker.TestHelper;
import com.vscoding.poker.WebsocketTestClient;
import com.vscoding.poker.boundary.bean.SessionCreationResponse;
import com.vscoding.poker.boundary.bean.UserResponse;
import com.vscoding.poker.boundary.bean.UserRequest;
import com.vscoding.poker.boundary.bean.VotingSessionResponse;
import com.vscoding.poker.entity.PokerSessionDAO;
import com.vscoding.poker.entity.UserDAO;
import com.vscoding.poker.utils.IdBuilder;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PokerControllerTest {

  @LocalServerPort
  private Integer port;

  @Autowired
  private PokerSessionDAO pokerSessionDAO;

  @Autowired
  private UserDAO userDAO;

  @Autowired
  private TestHelper testHelper;

  private WebsocketTestClient client;

  @BeforeEach
  public void setup() throws Exception {
    this.client = new WebsocketTestClient(port);
  }

  @Test
  @DisplayName("Check the creation of a new planning poker session - with new user")
  void createSession() throws Exception {
    // Given
    var request = new UserRequest(UUID.randomUUID().toString(), "temp");
    var blockingQueue = client.subscribe("/topic/personal/" + request.getPersonalToken(),
            SessionCreationResponse.class);

    // when
    client.send("/app/createSession/" + request.getPersonalToken(), request);
    var response = blockingQueue.poll(1, SECONDS);

    // then
    assertNotNull(response, "The server did not returned the creation confirmation.");
    assertNotNull(response.getSessionId());
    assertNotNull(response.getUserId());
    assertNotEquals(request.getPersonalToken(), response.getUserId());

    var session = pokerSessionDAO.findById(response.getSessionId()).orElse(null);
    var user = userDAO.findById(response.getUserId()).orElse(null);

    assertNotNull(session);
    assertNotNull(user);
    assertTrue(session.getOwner().contains(user));
    assertNotNull(session.getActiveStory());
  }

  @Test
  @DisplayName("Check the creation of a new planning poker session - with existing user")
  void createSessionExistingUser() throws Exception {
    // Given
    var user = testHelper.createUser();
    var request = new UserRequest(user.getId(), "temp");
    var blockingQueue = client.subscribe("/topic/personal/" + user.getId(),
            SessionCreationResponse.class);

    // when
    client.send("/app/createSession/" + request.getPersonalToken(), request);

    // then
    var response = blockingQueue.poll(1, SECONDS);

    assertNotNull(response, "The server did not returned the creation confirmation.");
    assertEquals(request.getPersonalToken(), response.getUserId());
  }

  @Test
  @DisplayName("Check the joining to the planning poker session - with new user")
  void joinSession() throws Exception {
    // Given
    var session = testHelper.createPokerSession();
    var id = IdBuilder.getUserId();
    var request = new UserRequest(id, id);
    var personalQueue = client.subscribe("/topic/personal/" + request.getPersonalToken(),
            UserResponse.class);
    var sessionQueue = client.subscribe("/topic/session/" + session.getId(),
            VotingSessionResponse.class);

    // when
    client.send("/app/join/" + session.getId(), request);

    // then
    var personalResponse = personalQueue.poll(1, SECONDS);

    assertNotNull(personalResponse, "The server did not returned the the confirmation.");
    assertNotNull(personalResponse.getUserId());
    assertTrue(userDAO.existsById(personalResponse.getUserId()));

    var sessionResponse = sessionQueue.poll(1, SECONDS);

    assertNotNull(sessionResponse);
    assertEquals(1, sessionResponse.getVotes().size());
    assertEquals(request.getUsername(), sessionResponse.getVotes().get(0).getName(),
            "User was not added as voter");
  }

  @Test
  @DisplayName("Check the joining to the planning poker session - with existing user")
  void joinSessionExistingUser() throws Exception {
    // Given
    var session = testHelper.createPokerSession();
    var user = testHelper.createUser();
    var request = new UserRequest(user.getId(), user.getName());
    var personalQueue = client.subscribe("/topic/personal/" + request.getPersonalToken(),
            UserResponse.class);

    // when
    client.send("/app/join/" + session.getId(), request);

    // then
    var personalResponse = personalQueue.poll(1, SECONDS);

    assertNotNull(personalResponse, "The server did not returned the join confirmation.");
    assertEquals(user.getId(), personalResponse.getUserId());
  }
}
