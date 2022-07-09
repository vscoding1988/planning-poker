package com.vscoding.poker.boundary.bean;

import lombok.Data;

/**
 * Response for voting
 */
@Data
public class VotingResponse {

  /**
   * Voting Id
   */
  private String voteId;

  /**
   * Name of the voter
   */
  private String name;

  /**
   * Has the user voted
   */
  private boolean voted;


  public VotingResponse(String voteId, String name) {
    this.voteId = voteId;
    this.name = name;
  }
}
