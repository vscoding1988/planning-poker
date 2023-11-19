package com.vscoding.poker.entity;

import java.util.Set;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Representation of a user story discussed in the planning session
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserStoryModel {

  /**
   * id
   */
  @Id
  String id;

  /**
   * User story name/description
   */
  String name;

  /**
   * Is true when all participants had voted, or when the owner has finished the story manually
   */
  boolean finished;

  /**
   * All topic votes
   */
  @ManyToMany(fetch = FetchType.EAGER)
  Set<VoteModel> participants;

  public UserStoryModel(String id, String name) {
    this.id = id;
    this.name = name;
  }
}
