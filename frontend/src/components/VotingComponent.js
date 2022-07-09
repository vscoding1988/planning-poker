import {useEffect} from "react";

function VotingComponent({sessionId, voteId, websocketClient}) {


  const sendVote = (value) => {
    if (websocketClient.current) {
      websocketClient.current.sendMessage("/app/vote/"+sessionId+"/"+voteId, value);
    } else {
      console.log("websocketClient is undefined")
    }
  }

  return (
      <>
        <section>
          <select onChange={(e) => sendVote(e.target.value)}>
            <option>1</option>
            <option>2</option>
            <option>3</option>
            <option>4</option>
          </select>
        </section>
      </>
  );
}

export default VotingComponent;
