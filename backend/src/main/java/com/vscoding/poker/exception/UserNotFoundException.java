package com.vscoding.poker.exception;

/**
 * Thrown when user not exists
 */
public class UserNotFoundException extends RuntimeException {

  public UserNotFoundException(String userId) {
    super("Could not find user with ID " + userId);
  }
}
