import SockJsClient from 'react-stomp';
import {useRef, useState, useEffect} from "react";
import {v4 as uuid} from 'uuid';

function WebSocketClient() {

  const [sessionId, setSessionId] = useState(null);
  const [user, setUser] = useState({
    id: uuid(),
    username: null
  });
  // TODO domain change
  const [socketUrl, setSocketUrl] = useState(
          'http://localhost:8080/ws-endpoint');

  const client = useRef(null);

  useEffect(() => {
    document.addEventListener('socket.SESSION_CREATION', onSessionCreate);
    let user = localStorage.getItem("user");

    if (user) {
      setUser(JSON.parse(user));
    }
    // TODO do I need an function for removing listener, if deps are empty?
  }, []);

  /**
   * Trigger app events based on the message type.
   * @param message
   */
  const onMessage = (message) => {
    const event = new CustomEvent("socket." + message.type, {
      detail: message
    });
    document.dispatchEvent(event);
  }

  /**
   * Send websocket request for creating a session.
   *
   * @param detail contains username
   */
  const onSessionCreate = ({detail}) => {
    client.current.sendMessage("/app/createSession/" + user.id, JSON.stringify({
      personalToken: user.id,
      username: detail.username
    }));
  }

  return (
          <>
            <SockJsClient url={socketUrl}
                          topics={["/topic/personal/" + user.id,
                            "/topic/session/" + sessionId]}
                          ref={(cl) => client.current = cl}
                          onMessage={onMessage}/>
          </>
  );
}

export default WebSocketClient;
