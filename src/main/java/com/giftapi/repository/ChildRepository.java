package com.giftapi.repository;

import com.giftapi.model.dto.ChildDTO;
import com.giftapi.model.entity.Child;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Stream;

import static org.hibernate.jpa.HibernateHints.HINT_FETCH_SIZE;

public interface ChildRepository extends JpaRepository<Child, Long>, JpaSpecificationExecutor<Child> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@EntityGraph(attributePaths = {"gifts"})
	Optional<Child> findWithLockingById(Long id);

	@Query(value = """
    SELECT c.id,
           c.first_name,
           c.last_name,
           c.birth_date,
           c.type,
           COALESCE(v.gift_count, 0) as gift_count
    FROM child c
    LEFT JOIN child_gift_count_view v ON c.id = v.child_id
    WHERE (:presentsCount IS NULL OR COALESCE(v.gift_count, 0) = :presentsCount)
    """,
			countQuery = """
    SELECT COUNT(c.id)
    FROM child c
    LEFT JOIN child_gift_count_view v ON c.id = v.child_id
    WHERE (:presentsCount IS NULL OR COALESCE(v.gift_count, 0) = :presentsCount)
    """,
			nativeQuery = true)
	Page<ChildDTO> findAllWithGiftCountFiltered(
			@Param("presentsCount") Long presentsCount,
			Pageable pageable
	);

	@QueryHints(value = @QueryHint(name = HINT_FETCH_SIZE, value = "100"))
	@Query("""
        SELECT DISTINCT c
        FROM Child c
        LEFT JOIN FETCH c.gifts g
        WHERE g.price > :priceThreshold
        ORDER BY c.id
        """)
	Stream<Child> streamChildrenWithExpensiveGifts(@Param("priceThreshold") BigDecimal priceThreshold);

}
