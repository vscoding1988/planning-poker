package com.vscoding.poker.exception;

/**
 * Thrown when session could not be found for given id
 */
public class SessionNotFoundException extends RuntimeException{

  public SessionNotFoundException(String sessionId) {
    super("Could not find session with ID "+sessionId);
  }
}
