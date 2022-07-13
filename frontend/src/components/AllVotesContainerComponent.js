function AllVotesContainerComponent({votes, user}) {

  function renderVotes() {
    if (votes) {
      return votes.map(vote => (
                      <li key={vote.name}> {vote.name} - {vote.voted
                              ? "has voted" : "has not voted"}</li>
              )
      )
    }
  }

  return (
          <div className="votes-display-container">
            <ul>
              {renderVotes()}
            </ul>
          </div>

  );
}

export default AllVotesContainerComponent;
