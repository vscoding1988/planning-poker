package com.vscoding.poker.boundary;

import com.google.gson.Gson;
import com.vscoding.poker.boundary.bean.SessionCreationResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.ByteArrayMessageConverter;
import org.springframework.messaging.converter.JsonbMessageConverter;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PokerControllerTest {
  @LocalServerPort
  private Integer port;

  private WebSocketStompClient webSocketStompClient;
  private StompSession session;

  @BeforeEach
  public void setup() throws Exception {
    this.webSocketStompClient = new WebSocketStompClient(new SockJsClient(
            List.of(new WebSocketTransport(new StandardWebSocketClient()))));
    webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());
    this.session = webSocketStompClient
            .connect(String.format("ws://localhost:%d/ws-endpoint", port), new StompSessionHandlerAdapter() {
            }).get(2, SECONDS);
  }

  @Test
  @DisplayName("Check the creation of a new planning poker session.")
  void createSession() throws Exception {
    // Given
    var token = UUID.randomUUID().toString();
    var blockingQueue = subscribe("/topic/personal/" + token, SessionCreationResponse.class);

    // when
    session.send("/app/createSession/" + token, "");

    // then
    var response = blockingQueue.poll(1, SECONDS);

    assertNotNull(response, "The server did not returned the creation confirmation.");
    assertNotNull(response.getSessionId());
    assertNotNull(response.getUserId());
  }

  @Test
  @DisplayName("Check the joining of the planning poker session.")
  void joinSession() throws Exception {
    // Given
    var token = UUID.randomUUID().toString();
    var blockingQueue = subscribe("/topic/personal/" + token, SessionCreationResponse.class);

    // when
    session.send("/app/createSession/" + token, "");

    // then
    var response = blockingQueue.poll(1, SECONDS);

    assertNotNull(response, "The server did not returned the creation confirmation.");
    assertNotNull(response.getSessionId());
    assertNotNull(response.getUserId());
  }

  /**
   * Subscribe to given endpoint
   *
   * @param endpoint endpoint to subscribe to
   * @param targetClass class of the expected message
   * @param <T> generic
   * @return Array with received message inside
   */
  private <T> ArrayBlockingQueue<T> subscribe(String endpoint, Class<T> targetClass) {
    var blockingQueue = new ArrayBlockingQueue<T>(1);

    session.subscribe(endpoint, new StompFrameHandler() {

      @Override
      public Type getPayloadType(StompHeaders headers) {
        // Not sure why the message is transferred as byte array
        return byte[].class;
      }

      @Override
      public void handleFrame(StompHeaders headers, Object payload) {
        var stringPayLoad = new String((byte[]) payload, StandardCharsets.UTF_8);
        var objPayload = new Gson().fromJson(stringPayLoad, targetClass);
        blockingQueue.add(objPayload);
      }
    });

    return blockingQueue;
  }
}
