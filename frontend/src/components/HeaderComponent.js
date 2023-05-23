import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {icon} from '@fortawesome/fontawesome-svg-core/import.macro'

/**
 * Renders the header
 *
 * @returns {JSX.Element}
 * @constructor
 */
function HeaderComponent() {

  function logout() {
    localStorage.setItem("user", "{}");

    window.location.href = "/";
  }

  function isLoggedIn() {
    let user = localStorage.getItem("user");

    if (user) {
      let username = JSON.parse(user).username;
      return username ? username.length === 0 : false;
    }

    return false;
  }

  return (
          <header className="flex">
            <h1>Planing Poker</h1>
            <span onClick={logout} style={{
              visibility: isLoggedIn() ? 'visible' : 'hidden'
            }}>
              <FontAwesomeIcon icon={icon({name: 'arrow-right-from-bracket'})}/>
            </span>

          </header>
  );
}

export default HeaderComponent;
