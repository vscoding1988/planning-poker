package com.vscoding.poker.entity;

import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Representation of a player
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserModel {

  /**
   * Generated UUID for user
   */
  @Id
  String id;

  /**
   * Username entered when first logged in
   */
  String name;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserModel userModel = (UserModel) o;
    return Objects.equals(id, userModel.id) && Objects.equals(name,
        userModel.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name);
  }
}
