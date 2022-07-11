package com.vscoding.poker.boundary.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * Representation of user creation request
 */
@Getter
@Setter
public class UserResponse extends BaseMassageResponse {

  /**
   * User id
   */
  private String userId;

  /**
   * Username
   */
  private String username;

  public UserResponse(String userId, String username) {
    super(MESSAGE_TYPE.USER_RESPONSE);
    this.userId = userId;
    this.username = username;
  }

  public UserResponse() {
    super(MESSAGE_TYPE.USER_RESPONSE);
  }
}
