package com.vscoding.poker.control;

import com.vscoding.poker.boundary.bean.SessionCreationResponse;
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

import java.util.UUID;

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

  public UserModel createNewUser(String username, String sessionId) {
    var sessionOpt = pokerSessionDAO.findById(sessionId);

    if (sessionOpt.isPresent()) {

      var user = new UserModel(UUID.randomUUID().toString(), username);
      userDAO.save(user);

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
        // TODO this may happen, when the user has his ID in the localStorage, but the user in DB was deleted,
        //  maybe instead of exception, the user should be recreated, but than I need a username to.
        //  For now I would expect the frontend to call user recreation
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
    }

    throw new SessionNotFoundException(sessionId);
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
      userStory = createNewUserStory("unnamed", session);
    }

    var previousVoting = userStory.getParticipants().stream()
            .filter(participant -> participant.getUserModel().equals(userModel))
            .findFirst()
            .orElse(null);

    if (previousVoting != null) {
      previousVoting.setVote(VoteModel.NOT_VOTED);
      voteDAO.save(previousVoting);
    } else {
      previousVoting = new VoteModel(UUID.randomUUID().toString(), VoteModel.NOT_VOTED, userModel);
      voteDAO.save(previousVoting);

      userStory.getParticipants().add(previousVoting);
      userStoryDAO.save(userStory);
    }
    return userModel;
  }

  /**
   * Create a new user story
   *
   * @param userStoryName name of the user story
   * @param session       planing poker session
   * @return {@link UserStoryModel}
   */
  public UserStoryModel createNewUserStory(String userStoryName, PokerSessionModel session) {
    var userStoryModel = new UserStoryModel(UUID.randomUUID().toString(), userStoryName);

    session.getUserStories().add(userStoryModel);
    session.setActiveStory(userStoryModel);

    userStoryDAO.save(userStoryModel);
    pokerSessionDAO.save(session);

    return userStoryModel;
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
   * @param personalToken this could be either a temporary generated key, or if a user already used the side his user token
   * @return
   */
  public SessionCreationResponse createNewSession(String personalToken) {
    return new SessionCreationResponse("1", "2");
  }
}
