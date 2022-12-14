package com.localhost.studybuddy.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserModel, Integer>, JpaSpecificationExecutor<UserModel> {

    boolean existsByEmail(String email);
    Optional<UserModel> findUserModelByEmail(String email);
    @Query(value = "select u from UserModel u inner join fetch u.roles where u.email = :email ")
    Optional<UserModel>findUserByEmailAndFetchRoles(String email);
    @Query(value = "UPDATE UserModel SET imagePath = :imagePath WHERE email = :email")
    @Modifying
    void updateUserImage(String email, String imagePath);

}
