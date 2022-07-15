/**
 *
 * @param votes {Array<Object>} {vote:string,name:string,voted:boolean}
 * @returns {JSX.Element}
 * @constructor
 */
function VotingComponent({votes}) {

  function renderVotes() {
    let groupedVotes = groupVotes();

    return Object.keys(groupedVotes).map(vote => (
            <div key={vote}>Vote {vote}: {groupedVotes[vote]}</div>))
  }

  /**
   * Group votes based of the vote to a list, sorted by vote count
   * @returns {Object}
   */
  function groupVotes() {
    let lookup = {};

    votes.map(vote => {
      if (!lookup[vote.vote]) {
        lookup[vote.vote] = 0;
      }
      lookup[vote.vote] = lookup[vote.vote] + 1;
    });

    return lookup;
  }

  return (
          <div className="voting-container">
            {renderVotes()}
          </div>

  );
}

export default VotingComponent;
