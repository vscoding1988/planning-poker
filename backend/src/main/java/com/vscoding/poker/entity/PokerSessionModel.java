package com.vscoding.poker.entity;

import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Represents a Planning Poker session
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PokerSessionModel {

  /**
   * Id of the session, is also used as segment
   */
  @Id
  String id;

  /**
   * The owner and creator of the session + everyone how gets rights by the owner
   */
  @ManyToMany
  Set<UserModel> owner;

  /**
   * list of user stories in the session
   */
  @OneToMany
  Set<UserStoryModel> userStories;

  /**
   * The current active user story
   */
  @OneToOne
  UserStoryModel activeStory;
}
