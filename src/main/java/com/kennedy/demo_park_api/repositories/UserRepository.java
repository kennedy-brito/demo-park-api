package com.kennedy.demo_park_api.repositories;

import com.kennedy.demo_park_api.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    @Query("SELECT u.role FROM User u WHERE u.username like ?1")
    User.Role findRoleByUsername(String username);
}
