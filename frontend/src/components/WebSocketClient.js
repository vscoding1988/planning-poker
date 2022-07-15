import SockJsClient from 'react-stomp';
import {useEffect, useRef, useState} from "react";
import {v4 as uuid} from 'uuid';

const personalToken = uuid();

function WebSocketClient() {

  const [sessionId] = useState(readSessionIdFromUrl);
  const [user, setUser] = useState(readUserFromLocalStorage);
  // TODO domain change
  const [socketUrl] = useState('http://localhost:8080/ws-endpoint');

  const client = useRef(null);

  /**
   * will register initial callbacks, we need to re register them, after user is
   * changed, because when page is loaded, the user will be 'null' this state is
   * persisted in the callbacks, and the setting of the user afterwards is not
   * propagated, so the user will stay null.
   */
  useEffect(() => {
    document.addEventListener('socket.SESSION_CREATION', onSessionCreation);
    document.addEventListener('socket.JOIN_SESSION_REQUEST', onJoinRequest);
    document.addEventListener('socket.USER_RESPONSE', onUserResponse);
    document.addEventListener('socket.VOTE_REQUEST', onVoteRequest);

    // The method will be executed before re-rendering. We want to remove all listener
    // before they are attached again (with actual user state)
    return () => {
      document.removeEventListener('socket.SESSION_CREATION', onSessionCreation);
      document.removeEventListener('socket.JOIN_SESSION_REQUEST', onJoinRequest);
      document.removeEventListener('socket.USER_RESPONSE', onUserResponse);
      document.removeEventListener('socket.VOTE_REQUEST', onVoteRequest);
    }
  }, [user]);

  /**
   * Trigger app events based on the message type. If USER_RESPONSE
   *
   * @param message payload from server, will contain type
   */
  const onMessage = (message) => {
    console.log("Websocket onMessage " + message.type + " current user "
            + JSON.stringify(user));
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

    if (!user || newUser.userId !== user.userId) {
      console.log("WebSocketClient reset user to" + JSON.stringify(newUser));
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

    client.current.sendMessage("/app/createSession/" + getUserId(),
            JSON.stringify({
              personalToken: getUserId(),
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
              personalToken: getUserId(),
              username: detail.username
            }));
  }

  /**
   * Send websocket request for voting in session.
   *
   * @param detail contains vote
   */
  function onVoteRequest({detail}){
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

    console.log("WebSocketClient set user to null");
    return null;
  }

  function getTopics() {
    let topics = [];

    if (sessionId) {
      // We are currently in a poker session, so we want to subscribe to the session feed
      topics.push("/topic/session/" + sessionId);
    }

    if (user) {
      topics.push("/topic/personal/" + user.userId);
    } else {
      // This is the first time we use the app, so we want to use a temporary feed
      // until we get the real user ID
      topics.push("/topic/personal/" + personalToken);
    }

    console.log("WebSocketClient Subscribe to " + JSON.stringify(topics));
    return topics;
  }

  function getUserId() {
    if (user && user.userId) {
      return user.userId;
    }
    return personalToken;
  }

  return (
          <>
            <SockJsClient url={socketUrl}
                          topics={getTopics()}
                          ref={(cl) => client.current = cl}
                          onConnect={onConnect}
                          onMessage={onMessage}/>
          </>
  );
}

export default WebSocketClient;
