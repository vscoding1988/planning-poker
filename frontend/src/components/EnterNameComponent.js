import {createRef} from "react";

/**
 * Renders the username input, when submitted will trigger socket.JOIN_SESSION_REQUEST
 * which is handled by {@link WebSocketClient}
 *
 * @returns {JSX.Element}
 * @constructor
 */
function EnterNameComponent() {
  const nameInput = createRef();

  /**
   * Trigger join session event. The event will be handled by {@link WebSocketClient}
   * and send message to the websocket. The server response will trigger,
   * socket.USER_RESPONSE which is handled in {@link VotingDisplayComponent}
   */
  const onCreationClick = () => {
    const sessionCreationEvent = new CustomEvent("socket.JOIN_SESSION_REQUEST", {
      detail: {
        "username": nameInput.current.value
      }
    });
    document.dispatchEvent(sessionCreationEvent);
    // TODO add loading bar or spinner
  }

  return (
          <>
            <input placeholder="Please enter your name." ref={nameInput}/>
            <button onClick={onCreationClick}>Join</button>
          </>
  );
}

export default EnterNameComponent;
