package com.strilets.reviews.Repository;

import com.strilets.reviews.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByUsername(String username);

    boolean getUserByEmail(String email);

    User findUserByEmail(String email);
}
