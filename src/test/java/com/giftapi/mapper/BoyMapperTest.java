package com.giftapi.mapper;

import com.giftapi.model.dto.BoyDTO;
import com.giftapi.model.dto.ChildDTO;
import com.giftapi.model.entity.Boy;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class BoyMapperTest {

	@Test
	void toDTO_shouldMapBoyEntityToBoyDTO() {
		// given
		Boy boy = Boy.builder()
				.id(1L)
				.firstName("John")
				.lastName("Doe")
				.birthDate(LocalDate.of(2015, 5, 10))
				.favoriteSport("Football")
				.build();

		// when
		ChildDTO result = BoyMapper.toDTO(boy);

		// then
		assertThat(result).isInstanceOf(BoyDTO.class);
		BoyDTO boyDTO = (BoyDTO) result;
		assertThat(boyDTO.getId()).isEqualTo(1L);
		assertThat(boyDTO.getFirstName()).isEqualTo("John");
		assertThat(boyDTO.getLastName()).isEqualTo("Doe");
		assertThat(boyDTO.getBirthDate()).isEqualTo(LocalDate.of(2015, 5, 10));
		assertThat(boyDTO.getType()).isEqualTo("BOY");
		assertThat(boyDTO.getFavoriteSport()).isEqualTo("Football");
	}

	@Test
	void toEntity_shouldMapPropertiesToBoyEntity() {
		// given
		Map<String, Object> properties = new HashMap<>();
		properties.put("firstName", "John");
		properties.put("lastName", "Doe");
		properties.put("birthDate", "2015-05-10");
		properties.put("favoriteSport", "Football");

		// when
		Boy result = (Boy) BoyMapper.toEntity(properties);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getFirstName()).isEqualTo("John");
		assertThat(result.getLastName()).isEqualTo("Doe");
		assertThat(result.getBirthDate()).isEqualTo(LocalDate.of(2015, 5, 10));
		assertThat(result.getFavoriteSport()).isEqualTo("Football");
	}

}

