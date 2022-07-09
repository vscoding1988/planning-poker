package com.vscoding.poker.entity;

import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
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
   * All topic votes
   */
  @ManyToMany
  Set<VoteModel> participants;

  public UserStoryModel(String id, String name) {
    this.id = id;
    this.name = name;
  }
}
