package com.vscoding.poker.boundary.bean;

import lombok.Data;

/**
 * Representation of user creation request
 */
@Data
public class UserCreationRequest {
  /**
   * Username of new user
   */
  private String username;
}
