import SockJsClient from 'react-stomp';
import {useEffect, useRef, useState} from "react";
import {v4 as uuid} from 'uuid';

function WebSocketClient() {

  const [sessionId] = useState(readSessionIdFromUrl);
  const [user, setUser] = useState(readUserFromLocalStorage);
  // TODO domain change
  const [socketUrl] = useState('http://localhost:8080/ws-endpoint');

  const client = useRef(null);

  useEffect(() => {
    document.addEventListener('socket.SESSION_CREATION', onSessionCreation);
    document.addEventListener('socket.JOIN_SESSION_REQUEST', onJoinRequest);
    document.addEventListener('socket.USER_RESPONSE', onUserResponse);
    document.addEventListener('socket.VOTE_REQUEST', onVoteRequest);
    // TODO do I need an function for removing listener, if deps are empty?
  }, []);

  /**
   * Trigger app events based on the message type. If USER_RESPONSE
   *
   * @param message payload from server, will contain type
   */
  const onMessage = (message) => {
    const event = new CustomEvent("socket." + message.type, {
      detail: message
    });
    document.dispatchEvent(event);
  }

  /**
   * Trigger event for connection established, this will make sure we send initial
   * request after socket is connected.
   */
  const onConnect = () => {
    console.log("Websocket connected.");
    const event = new CustomEvent("socket.CONNECTED", null);
    document.dispatchEvent(event);
  }

  /**
   * Callback for socket.USER_RESPONSE event. Persist the user in localStorage
   * and if userId has changed update the user state.
   *
   * @param detail contains type, userId, username
   */
  const onUserResponse = ({detail}) => {
    console.log("WebSocketClient onUserResponse");

    let newUser = {
      userId: detail.userId,
      username: detail.username
    }

    localStorage.setItem("user", JSON.stringify(newUser));

    if (newUser.userId !== user.userId) {
      setUser(newUser);
    }
  }

  /**
   * Send websocket request for creating a session.
   *
   * @param detail contains username
   */
  const onSessionCreation = ({detail}) => {
    console.log("WebSocketClient onSessionCreation");

    client.current.sendMessage("/app/createSession/" + user.userId,
            JSON.stringify({
              personalToken: user.userId,
              username: detail.username
            }));
  }

  /**
   * Send websocket request for joining a session.
   *
   * @param detail contains username
   */
  const onJoinRequest = ({detail}) => {
    console.log("WebSocketClient onJoinRequest");
    client.current.sendMessage("/app/join/" + sessionId,
            JSON.stringify({
              personalToken: user.userId,
              username: detail.username
            }));
  }

  /**
   * Send websocket request for voting in session.
   *
   * @param detail contains vote
   */
  const onVoteRequest = ({detail}) => {
    console.log("WebSocketClient onVoteRequest");
    client.current.sendMessage("/app/vote/" + sessionId + "/" + user.userId,
            detail.vote);
  }

  /**
   * If on the vote page, get session ID from the url.
   *
   * @returns {string}
   */
  function readSessionIdFromUrl() {
    let url = window.location.href;

    if (url.indexOf("/session/") > -1) {
      let split = url.split("/session/");
      return split[1];
    }
  }

  /**
   * If user already has used the app, he will have an old user written in localStorage, if not we would generate a
   * user with temporary id, the id will be overwritten when the user will enter his name.
   *
   * @returns {{userId: string, username: string}|any}
   */
  function readUserFromLocalStorage() {
    console.log("WebSocketClient readUserFromLocalStorage");
    let oldUserStr = localStorage.getItem("user");

    if (oldUserStr) {
      return JSON.parse(oldUserStr);
    }

    console.log("WebSocketClient readUserFromLocalStorage fallback to temp user");
    // TODO I am not setting the username, because if I do the state change
    //  in onUserResponse is not triggered, need to investigate that.
    return {
      userId: uuid()
    }
  }

  return (
          <>
            <SockJsClient url={socketUrl}
                          topics={["/topic/personal/" + user.userId,
                            "/topic/session/" + sessionId]}
                          ref={(cl) => client.current = cl}
                          onConnect={onConnect}
                          onMessage={onMessage}/>
          </>
  );
}

export default WebSocketClient;
