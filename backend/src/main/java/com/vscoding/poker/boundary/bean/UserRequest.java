package com.vscoding.poker.boundary.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representation of user creation request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
  /**
   * Token of joining user, could be a temporary token, or user id from previous sessions
   */
  private String personalToken;

  /**
   * Username of joining user
   */
  private String username;
}
