package com.giftapi.repository;

import com.giftapi.model.entity.Gift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GiftRepository extends JpaRepository <Gift, Long>{

	@Query("select count(g) from Gift g where g.child.id = :childId")
	long countGiftByChildId(@Param("childId") Long childId);

	Optional<Gift> findByIdAndChildId(Long id, Long childId);

}
