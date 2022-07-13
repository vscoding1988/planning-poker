package com.vscoding.poker.control;

import com.vscoding.poker.boundary.bean.UserResponse;
import com.vscoding.poker.boundary.bean.VotingResponse;
import com.vscoding.poker.boundary.bean.VotingSessionResponse;
import com.vscoding.poker.entity.PokerSessionModel;
import com.vscoding.poker.entity.UserModel;
import com.vscoding.poker.entity.VoteModel;
import java.util.ArrayList;
import java.util.Comparator;

public class ModelMapper {

  private ModelMapper() {
  }

  public static UserResponse toUserResponse(UserModel src) {
    return new UserResponse(src.getId(), src.getName());
  }

  public static VotingSessionResponse toVotingSessionResponse(PokerSessionModel src) {
    var result = new VotingSessionResponse();
    var activeStory = src.getActiveStory();

    if (activeStory != null) {
      result.setSessionId(src.getId());
      result.setUserStoryId(activeStory.getId());
      result.setUserStoryName(activeStory.getName());
      result.setFinished(activeStory.isFinished());

      var votes = new ArrayList<>(activeStory.getParticipants().stream()
              .map(vote -> toVotingResponse(vote, activeStory.isFinished()))
              .toList());
      votes.sort(Comparator.comparing(VotingResponse::getName));
      result.setVotes(votes);
    }

    return result;
  }

  /**
   * Transform {@link VoteModel} to {@link VotingResponse}
   *
   * @param src {@link VoteModel}
   * @param withVote if true the  {@link VotingResponse} will contain the vote
   * @return {@link VotingResponse}
   */
  public static VotingResponse toVotingResponse(VoteModel src, boolean withVote) {
    var votingResponse = new VotingResponse(src.getUserModel().getName());
    votingResponse.setVoted(!VoteModel.NOT_VOTED.equals(src.getVote()));

    if (withVote) {
      votingResponse.setVote(src.getVote());
    }

    return votingResponse;
  }
}
