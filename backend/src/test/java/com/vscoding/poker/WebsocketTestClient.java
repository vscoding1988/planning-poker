package com.vscoding.poker;

import static java.util.concurrent.TimeUnit.SECONDS;

import com.google.gson.Gson;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

/**
 *
 */
public class WebsocketTestClient {

  private final Integer port;
  private StompSession session;

  public WebsocketTestClient(Integer port) throws Exception {
    this.port = port;
    setUp();
  }

  protected void setUp() throws Exception {
    var webSocketStompClient = new WebSocketStompClient(new SockJsClient(
            List.of(new WebSocketTransport(new StandardWebSocketClient()))));
    webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

    this.session = webSocketStompClient
            .connect(String.format("ws://localhost:%d/ws-endpoint", port),
                    new StompSessionHandlerAdapter() {
                    }).get(2, SECONDS);
  }

  public void send(String endpoint, Object payload) {
    session.send(endpoint, payload);
  }


  /**
   * Subscribe to given endpoint
   *
   * @param endpoint    endpoint to subscribe to
   * @param targetClass class of the expected message
   * @param <T>         generic
   * @return Array with received message inside
   */
  public <T> ArrayBlockingQueue<T> subscribe(String endpoint, Class<T> targetClass) {
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
