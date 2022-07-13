import {useState} from "react";

function VotingComponent() {
  const [userVote, setUserVote] = useState(null);
  const voteOptions = ["0.5", "1", "2", "3", "5", "8", "13", "20"]

  function renderVoteOptions() {
    return voteOptions
    .map(vote => (<div
            className={userVote === vote ? "vote-object active": "vote-object"}
            onClick={() => onVote(vote)}>{vote}</div>))
  }

  const onVote = (option) => {
    const event = new CustomEvent("socket.VOTE_REQUEST", {
      detail: {
        "vote": option
      }
    });
    document.dispatchEvent(event);
    setUserVote(option);
  }

  // TODO add coffee/pause ect
  return (<div className="voting-container">
            {renderVoteOptions()}
          </div>

  );
}

export default VotingComponent;
