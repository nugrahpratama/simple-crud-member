package com.rama.crud.member.repository;

import com.rama.crud.member.domain.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    User findByEmail(String email);

    List<User> findAll(Sort ASC);

    Optional<User> findById(String id);

    void deleteById(String id);
}
