import {useEffect, useState} from "react";
import {v4 as uuid} from 'uuid';
import EnterNameComponent from "./EnterNameComponent";

function VotingSessionComponent() {

  const [user, setUser] = useState(readUserFromLocalStorage);

  useEffect(() => {
    document.addEventListener('socket.USER_RESPONSE', onUserResponse);
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

  return (
          <>
            <section className="main center">
              {
                user.username ? (
                        <h1>You are logged in {user.username}</h1>
                ) : (
                        <EnterNameComponent/>
                )
              }
              <div className="voting-container">

              </div>
              <div className="votes">

              </div>
            </section>
          </>
  );
}

export default VotingSessionComponent;
