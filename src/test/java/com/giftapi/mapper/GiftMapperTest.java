package com.giftapi.mapper;

import com.giftapi.model.dto.GiftDTO;
import com.giftapi.model.entity.Boy;
import com.giftapi.model.entity.Child;
import com.giftapi.model.entity.Gift;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class GiftMapperTest {

	@Test
	void toDto_shouldMapGiftEntityToGiftDTO() {
		// given
		Boy child = Boy.builder()
				.id(1L)
				.firstName("John")
				.lastName("Doe")
				.birthDate(LocalDate.of(2015, 5, 10))
				.favoriteSport("Football")
				.build();

		Gift gift = new Gift();
		gift.setId(10L);
		gift.setName("Toy Car");
		gift.setPrice(BigDecimal.valueOf(29.99));
		gift.setChild(child);

		// when
		GiftDTO result = GiftMapper.toDto(gift);

		// then
		assertThat(result).isNotNull();
		assertThat(result.id()).isEqualTo(10L);
		assertThat(result.name()).isEqualTo("Toy Car");
		assertThat(result.price()).isEqualTo(BigDecimal.valueOf(29.99));
		assertThat(result.childId()).isEqualTo(1L);
	}

	@Test
	void toDto_shouldMapGiftWithDifferentPrices() {
		// given
		Child child = Boy.builder()
				.id(2L)
				.firstName("Mike")
				.lastName("Smith")
				.birthDate(LocalDate.of(2014, 8, 20))
				.build();

		Gift gift = new Gift();
		gift.setId(20L);
		gift.setName("Video Game");
		gift.setPrice(BigDecimal.valueOf(59.99));
		gift.setChild(child);

		// when
		GiftDTO result = GiftMapper.toDto(gift);

		// then
		assertThat(result.price()).isEqualTo(BigDecimal.valueOf(59.99));
		assertThat(result.childId()).isEqualTo(2L);
	}

}

