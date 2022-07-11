import SockJsClient from 'react-stomp';
import {useRef, useState,useEffect} from "react";


function WebSocketClient() {

  const [sessionId, setSessionId] = useState(null);
  const [userId, setUserId] = useState(null);
  // TODO domain change
  const [socketUrl, setSocketUrl] = useState(
          'http://localhost:8080/ws-endpoint');

  const client = useRef(null);

  useEffect(() => {
    document.addEventListener('socket.SESSION_CREATION', onSessionCreate);

    // TODO do I need an function for removing listener, if deps are empty?
  }, []);

  /**
   * Trigger app events based on the message type.
   * @param e
   */
  const onMessage = (e) => {
    console.log("Message received.")
    console.log(e)
    // TODO check message type an dispatch based on type the the message
  }

  const onSessionCreate = ({detail}) => {
    console.log("onSessionCreate.")
    console.log(detail)
  }

  return (
          <>
            <SockJsClient url={socketUrl}
                          topics={["/topic/personal/" + userId,
                            "/topic/session/" + sessionId]}
                          ref={(cl) => client.current = cl}
                          onMessage={onMessage}/>
          </>
  );
}

export default WebSocketClient;
