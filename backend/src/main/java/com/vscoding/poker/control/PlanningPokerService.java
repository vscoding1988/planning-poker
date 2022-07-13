package com.vscoding.poker.control;

import com.vscoding.poker.boundary.bean.SessionCreationResponse;
import com.vscoding.poker.boundary.bean.UserRequest;
import com.vscoding.poker.entity.PokerSessionDAO;
import com.vscoding.poker.entity.PokerSessionModel;
import com.vscoding.poker.entity.UserDAO;
import com.vscoding.poker.entity.UserModel;
import com.vscoding.poker.entity.UserStoryDAO;
import com.vscoding.poker.entity.UserStoryModel;
import com.vscoding.poker.entity.VoteDAO;
import com.vscoding.poker.entity.VoteModel;
import com.vscoding.poker.exception.SessionNotFoundException;
import com.vscoding.poker.exception.UserNotFoundException;
import com.vscoding.poker.exception.UserStoryNotFoundException;
import com.vscoding.poker.utils.IdBuilder;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class PlanningPokerService {

  private final PokerSessionDAO pokerSessionDAO;
  private final VoteDAO voteDAO;
  private final UserStoryDAO userStoryDAO;
  private final UserDAO userDAO;

  public UserModel joinSession(UserRequest request, String sessionId) {
    var sessionOpt = pokerSessionDAO.findById(sessionId);

    if (sessionOpt.isPresent()) {

      var user = getOrCreateUser(request.getPersonalToken(), request.getUsername());

      return addUserToSession(user, sessionOpt.get());
    }

    throw new SessionNotFoundException(sessionId);
  }

  /**
   * submit a user vote
   *
   * @param userId    id of the user
   * @param vote      the vote of the user
   * @param sessionId id of the current session
   */
  public void addVote(String userId, String vote, String sessionId) {
    var sessionOpt = pokerSessionDAO.findById(sessionId);

    if (sessionOpt.isPresent()) {
      var session = sessionOpt.get();

      if (session.getActiveStory() == null) {
        throw new UserStoryNotFoundException("The session has no active user story");
      }

      var userOpt = userDAO.findById(userId);

      if (userOpt.isEmpty()) {
        // Normally this can't happen, because the user is created when he joins the session
        throw new UserNotFoundException(userId);
      }

      var user = userOpt.get();

      session.getActiveStory().getParticipants().stream()
              .filter(voteModel -> voteModel.getUserModel().equals(user))
              .forEach(voteModel -> {
                // Update vote
                voteModel.setVote(vote);
                voteDAO.save(voteModel);
              });
    }else{
      throw new SessionNotFoundException(sessionId);
    }
  }

  /**
   * To add a user to running session we have to, create a vote for him, and attach it to current
   * active story
   *
   * @param userModel {@link UserModel}
   * @param session   session the suer is currently voting
   * @return {@link UserModel}
   */
  private UserModel addUserToSession(UserModel userModel, PokerSessionModel session) {

    var userStory = session.getActiveStory();

    if (userStory == null) {
      throw new UserStoryNotFoundException("User story is not found.");
    }

    var previousVoting = userStory.getParticipants().stream()
            .filter(participant -> participant.getUserModel().equals(userModel))
            .findFirst()
            .orElse(null);

    if (previousVoting != null) {
      voteDAO.save(previousVoting);
    } else {
      // Add user as new voter
      previousVoting = new VoteModel(IdBuilder.getVoteId(), VoteModel.NOT_VOTED, userModel);
      voteDAO.save(previousVoting);

      userStory.getParticipants().add(previousVoting);
      userStoryDAO.save(userStory);
    }
    return userModel;
  }

  /**
   * Create a new user story and set it as active
   *
   * @param userStoryName name of the user story
   * @param session       planing poker session
   * @return {@link UserStoryModel}
   */
  public UserStoryModel createNewUserStory(String userStoryName, PokerSessionModel session) {
    var userStory = new UserStoryModel(IdBuilder.getUserStoryId(), userStoryName);

    if (session.getUserStories() == null) {
      session.setUserStories(new HashSet<>());
    }

    session.getUserStories().add(userStory);

    if (session.getActiveStory() != null) {
      // Add all participants of the old active story, but reset their votes
      var resetedParticipants = resetParticipants(session.getActiveStory().getParticipants());
      userStory.setParticipants(resetedParticipants);
    }

    session.setActiveStory(userStory);

    userStoryDAO.save(userStory);
    pokerSessionDAO.save(session);

    return userStory;
  }

  /**
   * Find poker session by id
   *
   * @param sessionId session id
   * @return optional of {@link PokerSessionModel}
   */
  public PokerSessionModel getSession(String sessionId) {
    return pokerSessionDAO.findById(sessionId)
            .orElseThrow(() -> new SessionNotFoundException(sessionId));
  }

  /**
   * Create a new session
   *
   * @param request holder for username and userid/temporary token
   * @return {@link SessionCreationResponse}
   */
  public SessionCreationResponse createNewSession(UserRequest request) {
    var user = getOrCreateUser(request.getPersonalToken(), request.getUsername());

    var pokerSessionModel = new PokerSessionModel(IdBuilder.getSessionId(), user);
    createNewUserStory("default", pokerSessionModel);

    return new SessionCreationResponse(pokerSessionModel.getId(), user.getId());
  }

  /**
   * Find or create a user
   *
   * @param userId   user id
   * @param username user name
   * @return {@link UserModel}
   */
  public UserModel getOrCreateUser(String userId, String username) {
    var userOpt = userDAO.findById(userId);

    if (userOpt.isEmpty()) {
      // Create new user
      var user = new UserModel(IdBuilder.getUserId(), username);
      userDAO.save(user);
      return user;
    }

    return userOpt.get();
  }

  /**
   * Create a new set with participants and reset their votes
   *
   * @param participants participants of the last story
   * @return Set {@link VoteModel}
   */
  private Set<VoteModel> resetParticipants(Set<VoteModel> participants) {
    var result = new HashSet<VoteModel>();

    participants.forEach(vote -> {
      var clone = new VoteModel(IdBuilder.getVoteId(), VoteModel.NOT_VOTED, vote.getUserModel());
      result.add(clone);
    });

    return result;
  }
}
