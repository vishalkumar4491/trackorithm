package com.trackorithm.track.modules.user.repo;

import com.trackorithm.track.modules.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    @Query("""
            select u
            from User u
            where lower(u.email) = lower(:identifier)
               or lower(u.username) = lower(:identifier)
               or u.phoneNumber = :identifier
            """)
    Optional<User> findByLoginIdentifier(@Param("identifier") String identifier);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByPhoneNumber(String phoneNumber);
}

