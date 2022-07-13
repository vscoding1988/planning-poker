package com.vscoding.poker.boundary.bean;

import lombok.Data;

/**
 * Response for voting
 */
@Data
public class VotingResponse {


  /**
   * Name of the voter
   */
  private String name;

  /**
   * the vote
   */
  private String vote;

  /**
   * Has the user voted
   */
  private boolean voted;

  public VotingResponse(String name) {
    this.name = name;
  }
}
