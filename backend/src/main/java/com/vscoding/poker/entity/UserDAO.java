package com.vscoding.poker.entity;

import org.springframework.data.repository.CrudRepository;

public interface UserDAO extends CrudRepository<UserModel,String> {

}
