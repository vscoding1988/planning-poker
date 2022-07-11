package com.vscoding.poker.boundary.bean;

import lombok.Data;

@Data
public abstract class BaseMassageResponse {

  protected enum MESSAGE_TYPE {
    VOTING_RESPONSE, USER_RESPONSE, SESSION_CREATION_RESPONSE
  }

  protected MESSAGE_TYPE type;

  protected BaseMassageResponse(MESSAGE_TYPE type) {
    this.type = type;
  }
}
