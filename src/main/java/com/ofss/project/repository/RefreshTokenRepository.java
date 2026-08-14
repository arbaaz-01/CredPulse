package com.ofss.project.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ofss.project.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

	Optional<RefreshToken> findByTokenHash(String tokenHash);
	
	@Modifying
	@Query("""
	    DELETE FROM RefreshToken r
	    WHERE r.expiresAt < :now
	       OR r.revoked = true
	""")
	int deleteExpiredOrRevokedTokens(
	        @Param("now") LocalDateTime now
	);

}
