package com.ofss.project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ofss.project.entity.CreditCard;
import com.ofss.project.enums.CardStatus;

public interface CreditCardRepository extends JpaRepository<CreditCard, Long> {

	List<CreditCard> findAllByUserId(Long userId);

	Optional<CreditCard> findByIdAndUserId(Long id, Long userId);

	boolean existsByUserIdAndCardNumberHash(Long userId, String cardNumberHash);
	
	List<CreditCard> findAllByUserIdAndStatusNot(
	        Long userId,
	        CardStatus status
	);

}
