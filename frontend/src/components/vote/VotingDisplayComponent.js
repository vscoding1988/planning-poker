import {Chart as ChartJS, ArcElement, Tooltip, Legend} from 'chart.js';
import {Pie} from 'react-chartjs-2';

/**
 *
 * @param votes {Array<Object>} {vote:string,name:string,voted:boolean}
 * @returns {JSX.Element}
 * @constructor
 */
ChartJS.register(ArcElement, Tooltip, Legend);

function VotingDisplayComponent({votes}) {

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

  /**
   * Get config for the PIE chart
   * @returns {{datasets: [{backgroundColor: string[], data: unknown[]}], labels: string[]}}
   */
  function getData() {
    let groupedVotes = groupVotes();

    return {
      labels: Object.keys(groupedVotes),
      datasets: [
        {
          data: Object.values(groupedVotes),
          backgroundColor: [
            'rgba(58,250,0,0.2)',
            'rgba(54, 162, 235, 0.2)',
            'rgba(255, 206, 86, 0.2)',
            'rgba(75, 192, 192, 0.2)',
            'rgba(153, 102, 255, 0.2)',
            'rgba(255, 159, 64, 0.2)',
          ],
        }
      ]
    }
  }

  return (
          <div className="voting-container">
            <Pie data={getData()}/>
          </div>

  );
}

export default VotingDisplayComponent;
