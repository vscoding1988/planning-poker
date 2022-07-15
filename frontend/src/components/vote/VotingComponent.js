function VotingComponent({userStoryId}) {
  const voteOptions = ["0.5", "1", "2", "3", "5", "8", "13", "20"]

  function renderVoteOptions() {
    let userVote = userStoryId ? getUserVote() : null;

    return voteOptions
    .map(vote => (<div
            key={vote}
            className={userVote === vote ? "vote-object active" : "vote-object"}
            onClick={() => onVote(vote)}>{vote}</div>))
  }

  const onVote = (option) => {
    // Send server update
    const event = new CustomEvent("socket.VOTE_REQUEST", {
      detail: {
        "vote": option
      }
    });
    document.dispatchEvent(event);

    // persist vote in LocalStorage
    let vote = JSON.parse(localStorage.getItem("votes"));
    vote[userStoryId] = option;
    localStorage.setItem("votes", JSON.stringify(vote));
  }

  /**
   * Votes are persisted in the localStorage with key = $userStoryId and value
   * the vote.
   */
  function getUserVote() {
    let votesStr = localStorage.getItem("votes");

    if (!votesStr) {
      votesStr = "{}";
    }

    let vote = JSON.parse(votesStr);

    if (vote[userStoryId]) {
      return vote[userStoryId];
    } else {
      vote[userStoryId] = null;
      localStorage.setItem("votes", JSON.stringify(vote));
      return null;
    }
  }

// TODO add coffee/pause ect
  return (<div className="voting-container">
            {renderVoteOptions()}
          </div>

  );
}

export default VotingComponent;
