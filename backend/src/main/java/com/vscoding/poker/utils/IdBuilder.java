package com.vscoding.poker.utils;

import java.util.UUID;

public class IdBuilder {

  private IdBuilder() {
  }

  public static String getSessionId() {
    return getId();
  }
  public static String getUserStoryId() {
    return getId();
  }

  public static String getUserId() {
    return getId();
  }

  public static String getVoteId() {
    return getId();
  }

  private static String getId() {
    return UUID.randomUUID().toString();
  }
}
