package com.vscoding.poker.boundary.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * Representation of user creation request
 */
@Getter
@Setter
public class UserCreationResponse extends BaseMassageResponse{
  /**
   * User id
   */
  private String userId;
  /**
   * Vote Id for the user
   */
  private String voteId;

  public UserCreationResponse(String userId, String voteId) {
    super(MESSAGE_TYPE.USER_CREATION_RESPONSE);
    this.userId = userId;
    this.voteId = voteId;
  }

  public UserCreationResponse() {
    super(MESSAGE_TYPE.USER_CREATION_RESPONSE);
  }
}
