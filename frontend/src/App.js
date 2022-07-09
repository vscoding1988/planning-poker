import './App.css';
import SockJsClient from 'react-stomp';
import {useRef, useState} from "react";
import VotingComponent from "./components/VotingComponent";
import VotingDisplayComponent from "./components/VotingDisplayComponent";

function App() {

  /**
   * Is part of the url
   */
  const [sessionId] = useState('12');

  /**
   * Will be generated, when user is registered
   */
  const [voteId, setVoteId] = useState(null);
  const [socketUrl, setSocketUrl] = useState('http://localhost:8080/vote');

  const client = useRef(null);
  const votingDisplayComponent = useRef();

  /**
   * Send a request to register your self to the app
   */
  const onConnect = () => {
    if(!voteId){
      client.sendMessage("/app/newUser/"+sessionId, {
        username:"temp"
      });
    }
  }

  return (
      <>
        <SockJsClient url={socketUrl}
                      topics={["/topic/vote/"+sessionId]}
                      ref={(cl) => client.current = cl}
                      onConnect={() => onConnect()}/>
                      onMessage={(e) => votingDisplayComponent.current.onMessageReceive(e)}/>

        <section>
          <VotingDisplayComponent ref={votingDisplayComponent}/>
          <VotingComponent sessionId={sessionId} voteId={voteId} websocketClient={client}/>
        </section>
      </>
  );
}

export default App;
