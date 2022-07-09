package com.vscoding.poker.exception;

/**
 * Is thrown when there is no active user story in session (f.e. because all were finished) or the
 * next story was not started
 */
public class UserStoryNotFoundException extends RuntimeException {

  public UserStoryNotFoundException(String message) {
    super(message);
  }
}
