package com.example.jpatest.repository;

import com.example.jpatest.model.User;
import org.springframework.data.repository.CrudRepository;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-07-05 14:09
 */
public interface  UserRepository  extends CrudRepository<User, Long> {

}
