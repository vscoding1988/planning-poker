import {createRef, useEffect, useState} from "react";
import HeaderComponent from "./HeaderComponent";

/**
 * Renders the session creation page. If the suer already has user has never used the
 * app, he will be asked to enter his name, if the user already has used the app,
 * he will be greeted.
 *
 * @returns {JSX.Element}
 * @constructor
 */
function CreateSessionComponent() {
  const [username] = useState(getUser);
  const nameInput = createRef();

  useEffect(() => {
    document.addEventListener('socket.SESSION_CREATION_RESPONSE',
            onCreationResponse);

    // TODO do I need an function for removing listener, if deps are empty?
  }, []);

  /**
   * If the user has already used the app, he will have a userId and username in the localStorage, if not the username will
   * stay empty, so that the input field for the username is rendered.
   *
   * @returns {string|any}
   */
  function getUser() {
    let oldUserStr = localStorage.getItem("user");

    if (oldUserStr) {
      return JSON.parse(oldUserStr).username;
    }

    return null;
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
    console.log("CreateSessionComponent onCreationResponse: session "
            + detail.sessionId + " created");

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
          <>
            <HeaderComponent/>
            <section className="main center create-session-wrapper">
              {username ?
                      (<h2 data-test-id="create-session-name" className="greetings">Hi {username}</h2>) :
                      (<input placeholder="Please enter your name."
                              ref={nameInput}
                              data-test-id="create-session-input-name"/>)
              }
              <button onClick={onCreationClick}
                      className="create-session-btn"
                      data-test-id="create-session-submit">Create new session
              </button>
            </section>
          </>
  );
}

export default CreateSessionComponent;
