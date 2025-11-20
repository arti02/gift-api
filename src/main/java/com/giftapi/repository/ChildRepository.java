package com.giftapi.repository;

import com.giftapi.model.Child;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChildRepository extends JpaRepository<Child, Long> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select c from Child c where c.id = :id")
	Optional<Child> findByIdWithLock(@Param("id") Long id);
}
