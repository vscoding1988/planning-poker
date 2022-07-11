package com.vscoding.poker.boundary.bean;

import lombok.Data;

/**
 * Response for voting
 */
@Data
public class VotingResponse {

  /**
   * User Id
   */
  private String userId;

  /**
   * Name of the voter
   */
  private String name;

  /**
   * Has the user voted
   */
  private boolean voted;


  public VotingResponse(String userId, String name) {
    this.userId = userId;
    this.name = name;
  }
}
