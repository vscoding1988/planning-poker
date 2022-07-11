package com.vscoding.poker;

import com.vscoding.poker.entity.PokerSessionDAO;
import com.vscoding.poker.entity.PokerSessionModel;
import com.vscoding.poker.entity.UserDAO;
import com.vscoding.poker.entity.UserModel;
import com.vscoding.poker.entity.UserStoryDAO;
import com.vscoding.poker.entity.UserStoryModel;
import java.util.Collections;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TestHelper {

  private final PokerSessionDAO pokerSessionDAO;
  private final UserStoryDAO userStoryDAO;
  private final UserDAO userDAO;

  public PokerSessionModel createPokerSession() {
    var sessionId = UUID.randomUUID().toString();

    var owner = createUser();
    var session = new PokerSessionModel(sessionId, owner);
    var userStory = new UserStoryModel(UUID.randomUUID().toString(), "");

    session.setActiveStory(userStory);
    session.setUserStories(Collections.singleton(userStory));

    userStoryDAO.save(userStory);
    pokerSessionDAO.save(session);

    return session;
  }

  public UserModel createUser(){
    var id = UUID.randomUUID().toString();
    var user = new UserModel(id, id);

    return userDAO.save(user);
  }
}
