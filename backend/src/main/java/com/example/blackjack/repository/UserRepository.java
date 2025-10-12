package com.example.blackjack.repository;

import com.example.blackjack.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE " +
            "r.name = 'ROLE_USER' AND " +
            "u.id NOT IN (SELECT u2.id FROM User u2 JOIN u2.roles r2 WHERE r2.name = 'ROLE_ADMIN') AND " +
            "u.handsPlayed > 0 " +
            "ORDER BY (CAST(u.handsWon AS double) / u.handsPlayed) DESC, u.handsPlayed DESC " +
            "LIMIT 10")
    List<User> findTop10LeaderboardUsersByWinRate();
}