package com.vscoding.poker.entity;

import java.util.Collections;
import java.util.Set;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
  @ManyToMany(fetch = FetchType.EAGER)
  Set<UserModel> owner;

  /**
   * list of user stories in the session
   */
  @OneToMany
  Set<UserStoryModel> userStories;

  /**
   * The current active user story
   */
  @OneToOne(fetch = FetchType.EAGER)
  UserStoryModel activeStory;

  public PokerSessionModel(String id, UserModel owner) {
    this.id = id;
    this.owner = Collections.singleton(owner);
  }
}
