package com.vscoding.poker.boundary.bean;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class VotingSessionResponse extends BaseMassageResponse{

  /**
   * list of votings
   */
  private List<VotingResponse> votes;

  public VotingSessionResponse(List<VotingResponse> votes) {
    super(MESSAGE_TYPE.VOTING_RESPONSE);
    this.votes = votes;
  }

  public VotingSessionResponse() {
    super(MESSAGE_TYPE.VOTING_RESPONSE);
  }
}
