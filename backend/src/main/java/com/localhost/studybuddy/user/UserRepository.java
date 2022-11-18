package com.localhost.studybuddy.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserModel, Integer> {

    Optional<UserModel> findUserModelByEmail(String email);
    boolean existsByEmail(String email);

}
