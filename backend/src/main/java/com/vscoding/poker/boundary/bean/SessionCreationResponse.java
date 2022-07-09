package com.vscoding.poker.boundary.bean;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SessionCreationResponse extends BaseMassageResponse {

  /**
   * Id of the newly created session
   */
  String sessionId;
  /**
   * ID of the newly created user
   */
  String userId;

  protected SessionCreationResponse(String sessionId, String userId) {
    super(MESSAGE_TYPE.SESSION_CREATION_RESPONSE);
    this.sessionId = sessionId;
    this.userId = userId;
  }
}
