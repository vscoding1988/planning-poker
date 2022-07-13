package com.vscoding.poker.boundary.bean;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VotingSessionResponse extends BaseMassageResponse {

  /**
   * list of votings
   */
  private List<VotingResponse> votes;

  /**
   * Session id
   */
  private String sessionId;

  /**
   * Current active user story id
   */
  private String userStoryId;

  /**
   * Current active user story name
   */
  private String userStoryName;

  public VotingSessionResponse() {
    super(MESSAGE_TYPE.VOTING_RESPONSE);
  }
}
