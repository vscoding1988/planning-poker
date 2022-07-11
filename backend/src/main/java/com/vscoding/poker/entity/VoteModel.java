package com.vscoding.poker.entity;

import java.util.Arrays;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Representation of user vote for a user story
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VoteModel {

  public static final String NOT_VOTED = "NAN";
  protected static final List<String> SPECIAL_VOTES = Arrays.asList("COFFEE", "QUESTION", NOT_VOTED);

  @Id
  String id;

  /**
   * User vote
   */
  String vote;

  /**
   * The user how has given the vote
   */
  @ManyToOne(fetch = FetchType.EAGER)
  UserModel userModel;
}
