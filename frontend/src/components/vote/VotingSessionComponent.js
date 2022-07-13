import {useEffect, useState} from "react";
import {v4 as uuid} from 'uuid';
import EnterNameComponent from "./EnterNameComponent";
import AllVotesContainerComponent from "./AllVotesContainerComponent";
import VotingComponent from "./VotingComponent";

function VotingSessionComponent() {

  const [user, setUser] = useState(readUserFromLocalStorage);
  const [session, setSession] = useState(null);

  useEffect(() => {
    document.addEventListener('socket.USER_RESPONSE', onUserResponse);
    document.addEventListener('socket.VOTING_RESPONSE', onVotingResponse);
    document.addEventListener('socket.CONNECTED', onConnected);
    // TODO do I need an function for removing listener, if deps are empty?
  }, []);

  /**
   * If user already has used the app, he will have an old user written in localStorage,
   * if not we would generate a user with temporary id, the id will be
   * overwritten when the user will enter his name.
   *
   * @returns {{userId: string, username: string}|any}
   */
  function readUserFromLocalStorage() {
    let oldUserStr = localStorage.getItem("user");

    if (oldUserStr) {
      return JSON.parse(oldUserStr);
    }

    return {
      userId: uuid(),
      username: null
    }
  }

  /**
   * Callback for socket.USER_RESPONSE event. If userId is different from user in
   * state we update the user state.
   *
   * @param detail contains type, userId, username
   */
  const onUserResponse = ({detail}) => {
    if (detail.userId !== user.userId) {

      setUser({
        userId: detail.userId,
        username: detail.username
      });
    }
  }

  /**
   * IS triggered by {@link WebSocketClient} when a change in voting is registered.
   *
   * @param detail contains type, userId, username
   */
  const onVotingResponse = ({detail}) => {
    setSession(detail);
  }

  const onConnected = () => {
    if (user.username && !session) {
      // User exists so we can trigger join session directly
      const event = new CustomEvent("socket.JOIN_SESSION_REQUEST", {
        detail: {
          "username": user.username
        }
      });
      document.dispatchEvent(event);
    }
  }

  return (
          <>
            {
              user.username ? (
                      <section className="voting-session-container">
                        <VotingComponent
                                userStoryId={session ? session.userStoryId
                                        : null}/>
                        <AllVotesContainerComponent
                                votes={session ? session.votes : []}
                                user={user}/>
                      </section>
              ) : (
                      <EnterNameComponent/>
              )
            }
          </>
  );
}

export default VotingSessionComponent;
