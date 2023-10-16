package com.example.securitycustom.repository;

import com.example.securitycustom.model.RefreshToken;
import com.example.securitycustom.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

/**
 * @author NamTv
 * @since 13/10/2023
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    @Query(value = "SELECT * \n" +
            "FROM refreshtoken \n" +
            "WHERE user_id = ?1 ", nativeQuery = true)
    RefreshToken checkExistingToken(String userId);

    @Modifying
    void deleteByUser(User user);
}
