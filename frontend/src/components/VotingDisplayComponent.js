import {forwardRef, useEffect, useImperativeHandle, useState} from "react";

const VotingDisplayComponent = forwardRef((props, ref) => {

  const [response, setResponse] = useState({votes: []});

  useImperativeHandle(ref, () => ({

    onMessageReceive(e) {
      console.log("Received message.");
      setResponse(e);
    }
  }));

  return (
      <ul>
        {
          response.votes.map(vote => (
              <li key={vote.name}> {vote.name} - {vote.voted ? "has voted": "has not voted"}</li>
          ))
        }
      </ul>
  );
});

export default VotingDisplayComponent;
