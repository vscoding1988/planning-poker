import SockJsClient from 'react-stomp';
import {useEffect, useRef, useState} from "react";
import {v4 as uuid} from 'uuid';

function WebSocketClient() {

  const [sessionId] = useState(readSessionIdFromUrl);
  const [user, setUser] = useState({
    userId: uuid(),
    username: null
  });
  // TODO domain change
  const [socketUrl] = useState('http://localhost:8080/ws-endpoint');

  const client = useRef(null);

  useEffect(() => {
    document.addEventListener('socket.SESSION_CREATION', onSessionCreation);
    document.addEventListener('socket.USER_RESPONSE', onUserResponse);
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
   * Callback for socket.USER_RESPONSE event. Persist the user in localStorage
   * and if userId has changed update the user state.
   *
   * @param message contains type, userId, username
   */
  const onUserResponse = (message) => {
    let newUser = {
      userId: message.userId,
      username: message.username
    }

    localStorage.setItem("user", JSON.stringify(newUser));

    if(newUser.userId !== user.userId){
      setUser(message);
    }
  }

  /**
   * Send websocket request for creating a session.
   *
   * @param detail contains username
   */
  const onSessionCreation = ({detail}) => {
    client.current.sendMessage("/app/createSession/" + user.userId,
            JSON.stringify({
              personalToken: user.userId,
              username: detail.username
            }));
  }

  /**
   * If on the vote page, get session ID from the url.
   *
   * @returns {string}
   */
  function readSessionIdFromUrl() {
    let url = window.location.href;

    if(url.indexOf("/session/") > -1){
      let split = url.split("/session/");
      return split[1];
    }
  }

  /**
   * Will check if a user in localStorage exists and if one is found, validate it.
   */
  function checkForExistingUser() {
    let oldUserStr = localStorage.getItem("user");

    if (oldUserStr) {
      let oldUser = JSON.parse(oldUserStr);
      // When user exists we have to validate him, we will use the user.userId
      // as long the user is not validated
      client.current.sendMessage("/app/validateUser/" + user.userId,
              JSON.stringify({
                personalToken: oldUser.userId,
                username: oldUser.username
              }));
    }
  }

  return (
          <>
            <SockJsClient url={socketUrl}
                          topics={["/topic/personal/" + user.userId,
                            "/topic/session/" + sessionId]}
                          ref={(cl) => client.current = cl}
                          onConnect={checkForExistingUser}
                          onMessage={onMessage}/>
          </>
  );
}

export default WebSocketClient;
