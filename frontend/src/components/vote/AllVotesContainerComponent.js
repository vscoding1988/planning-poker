function AllVotesContainerComponent({votes}) {

  function renderVotes() {
    if (votes) {
      return votes.map((vote,index) => (
                      <li key={vote.name+index}> {vote.name} - {vote.voted
                              ? "has voted" : "has not voted"} {vote.vote}</li>
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
