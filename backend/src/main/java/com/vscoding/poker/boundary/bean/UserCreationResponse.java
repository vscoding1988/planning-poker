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

  public UserCreationResponse(String userId) {
    super(MESSAGE_TYPE.USER_CREATION_RESPONSE);
    this.userId = userId;
  }

  public UserCreationResponse() {
    super(MESSAGE_TYPE.USER_CREATION_RESPONSE);
  }
}
