package com.giftapi.mapper;

import com.giftapi.model.dto.ChildDTO;
import com.giftapi.model.entity.Child;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ChildMapperTest {

	@Test
	void toDTO_shouldMapChildEntityToChildDTO() {
		// given
		Child child = Child.builder()
				.id(1L)
				.firstName("John")
				.lastName("Doe")
				.birthDate(LocalDate.of(2015, 5, 10))
				.build();

		// when
		ChildDTO result = ChildMapper.toDTO(child);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(1L);
		assertThat(result.getFirstName()).isEqualTo("John");
		assertThat(result.getLastName()).isEqualTo("Doe");
		assertThat(result.getBirthDate()).isEqualTo(LocalDate.of(2015, 5, 10));
		assertThat(result.getType()).isEqualTo("CHILD");
	}

	@Test
	void toEntity_shouldMapPropertiesToChildEntity() {
		// given
		Map<String, Object> properties = new HashMap<>();
		properties.put("firstName", "John");
		properties.put("lastName", "Doe");
		properties.put("birthDate", "2015-05-10");

		// when
		Child result = ChildMapper.toEntity(properties);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getFirstName()).isEqualTo("John");
		assertThat(result.getLastName()).isEqualTo("Doe");
		assertThat(result.getBirthDate()).isEqualTo(LocalDate.of(2015, 5, 10));
	}

}

