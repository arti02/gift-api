package com.giftapi.repository;

import com.giftapi.model.Gift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GiftRepository extends JpaRepository <Gift, Long>{

	@Query("select count(g) from Gift g where g.child.id = :childId")
	long countGiftByChildId(@Param("childId") Long childId);

	Optional<Gift> findByIdAndChildId(Long id, Long childId);

}
