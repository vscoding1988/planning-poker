import {createRef, useEffect, useState} from "react";

/**
 * Renders the session creation page. If the suer already has user has never used the
 * app, he will be asked to enter his name, if the user already has used the app,
 * he will be greeted.
 *
 * @returns {JSX.Element}
 * @constructor
 */
function CreateSessionComponent() {
  const [username, setUsername] = useState(null);
  const nameInput = createRef();

  useEffect(() => {
    document.addEventListener('socket.USER_RESPONSE', onUserResponse);
    document.addEventListener('socket.SESSION_CREATION_RESPONSE',
            onCreationResponse);

    // TODO do I need an function for removing listener, if deps are empty?
  }, []);

  /**
   * On load the {@link WebSocketClient} will check the localStorage and if there
   * is a username + userid, it will send a message to the server to check if the
   * user is still valid and trigger the event socket.USER_RESPONSE. If the user
   * uses the app for the first time, this won't be called.
   *
   * TODO maybe instead check the localStorage by our self?
   * @param detail contains username
   */
  const onUserResponse = ({detail}) => {
    setUsername(detail.username);
  }

  /**
   * After session creation the {@link WebSocketClient} will trigger the
   * socket.SESSION_CREATION_RESPONSE event, which will execute this callback.
   * The payload contains the session id, so we can use it to redirect the user
   * to the /session/$sessionId.
   *
   * @param detail contains the userId and sessionId
   */
  const onCreationResponse = ({detail}) => {
    if (!username) {
      let newUser = {
        "username": nameInput.current.value,
        "userId": detail.userId
      }
      localStorage.setItem("user", JSON.stringify(newUser));
    }
    window.open("/session/" + detail.sessionId, "_self");
  }

  /**
   * Trigger session creation event. The event will be handled by {@link WebSocketClient}
   * and send message to the websocket. When the server has finished the creation,
   * {@link WebSocketClient} will trigger socket.SESSION_CREATION_RESPONSE, which will
   * call the {@link CreateSessionComponent#onCreationResponse} callback
   */
  const onCreationClick = () => {
    const sessionCreationEvent = new CustomEvent("socket.SESSION_CREATION", {
      detail: {
        "username": username ? username : nameInput.current.value
      }
    });
    document.dispatchEvent(sessionCreationEvent);
    // TODO add loading bar or spinner
  }

  return (
          <section className="main center">
            {username ?
                    (<h1>Hi {username}</h1>) :
                    (<input placeholder="Please enter your name."
                            ref={nameInput}/>)
            }
            <button onClick={onCreationClick}>Create new session</button>
          </section>
  );
}

export default CreateSessionComponent;
