package com.vscoding.poker.control;

import com.vscoding.poker.boundary.bean.UserCreationResponse;
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

  public static UserCreationResponse toUserCreationResponse(UserModel src) {
    return new UserCreationResponse(src.getId());
  }

  public static VotingSessionResponse toVotingSessionResponse(PokerSessionModel src) {
    var result = new VotingSessionResponse();
    var activeStory = src.getActiveStory();

    if (activeStory != null) {

      var votes = new ArrayList<>(activeStory.getParticipants().stream()
              .map(ModelMapper::toVotingResponse)
              .toList());
      votes.sort(Comparator.comparing(VotingResponse::getName));
      result.setVotes(votes);
    }

    return result;
  }

  public static VotingResponse toVotingResponse(VoteModel src) {
    var votingResponse = new VotingResponse(src.getUserModel().getId(), src.getUserModel().getName());
    votingResponse.setVoted(!VoteModel.NOT_VOTED.equals(src.getVote()));
    return votingResponse;
  }
}
