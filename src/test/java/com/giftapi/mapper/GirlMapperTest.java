package com.giftapi.mapper;

import com.giftapi.model.dto.ChildDTO;
import com.giftapi.model.dto.GirlDTO;
import com.giftapi.model.entity.Girl;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GirlMapperTest {

	@Test
	void toDTO_shouldMapGirlEntityToGirlDTO() {
		// given
		Girl girl = Girl.builder()
				.id(1L)
				.firstName("Alice")
				.lastName("Smith")
				.birthDate(LocalDate.of(2016, 3, 15))
				.dressColor("Pink")
				.build();

		// when
		ChildDTO result = GirlMapper.toDTO(girl);

		// then
		assertThat(result).isInstanceOf(GirlDTO.class);
		GirlDTO girlDTO = (GirlDTO) result;
		assertThat(girlDTO.getId()).isEqualTo(1L);
		assertThat(girlDTO.getFirstName()).isEqualTo("Alice");
		assertThat(girlDTO.getLastName()).isEqualTo("Smith");
		assertThat(girlDTO.getBirthDate()).isEqualTo(LocalDate.of(2016, 3, 15));
		assertThat(girlDTO.getType()).isEqualTo("GIRL");
		assertThat(girlDTO.getDressColor()).isEqualTo("Pink");
	}

	@Test
	void toEntity_shouldMapPropertiesToGirlEntity() {
		// given
		Map<String, Object> properties = new HashMap<>();
		properties.put("firstName", "Alice");
		properties.put("lastName", "Smith");
		properties.put("birthDate", "2016-03-15");
		properties.put("dressColor", "Pink");

		// when
		Girl result = (Girl) GirlMapper.toEntity(properties);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getFirstName()).isEqualTo("Alice");
		assertThat(result.getLastName()).isEqualTo("Smith");
		assertThat(result.getBirthDate()).isEqualTo(LocalDate.of(2016, 3, 15));
		assertThat(result.getDressColor()).isEqualTo("Pink");
	}
}

