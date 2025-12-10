package com.giftapi.service.operations;

import com.giftapi.model.dto.ChildDTO;
import com.giftapi.model.dto.GirlDTO;
import com.giftapi.model.dto.command.child.CreateChildCommand;
import com.giftapi.model.dto.command.child.UpdateChildCommand;
import com.giftapi.model.entity.Child;
import com.giftapi.model.entity.Girl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class GirlOperationsTest {

	@InjectMocks
	private GirlOperations classUnderTest;

	@Test
	void create_shouldCreateGirlFromCommand() {
		// given
		Map<String, Object> properties = new HashMap<>();
		properties.put("firstName", "Alice");
		properties.put("lastName", "Smith");
		properties.put("birthDate", "2016-03-15");
		properties.put("dressColor", "Pink");

		CreateChildCommand command = new CreateChildCommand(properties);

		// when
		Child result = classUnderTest.create(command);

		// then
		assertThat(result).isInstanceOf(Girl.class);
		Girl girl = (Girl) result;
		assertThat(girl.getFirstName()).isEqualTo("Alice");
		assertThat(girl.getLastName()).isEqualTo("Smith");
		assertThat(girl.getBirthDate()).isEqualTo(LocalDate.of(2016, 3, 15));
		assertThat(girl.getDressColor()).isEqualTo("Pink");
	}

	@Test
	void update_shouldUpdateGirlFromCommand() {
		// given
		Girl existingGirl = Girl.builder()
				.id(1L)
				.firstName("Alice")
				.lastName("Smith")
				.birthDate(LocalDate.of(2016, 3, 15))
				.dressColor("Pink")
				.build();

		Map<String, Object> properties = new HashMap<>();
		properties.put("firstName", "Alicia");
		properties.put("lastName", "Smith");
		properties.put("birthDate", "2016-03-15");
		properties.put("dressColor", "Purple");

		UpdateChildCommand command = new UpdateChildCommand(properties);

		// when
		Child result = classUnderTest.update(command, existingGirl);

		// then
		assertThat(result).isInstanceOf(Girl.class);
		Girl girl = (Girl) result;
		assertThat(girl.getId()).isEqualTo(1L); // ID should be preserved
		assertThat(girl.getFirstName()).isEqualTo("Alicia");
		assertThat(girl.getLastName()).isEqualTo("Smith");
		assertThat(girl.getDressColor()).isEqualTo("Purple");
	}

	@Test
	void mapToDTO_shouldMapGirlToDTO() {
		// given
		Girl girl = Girl.builder()
				.id(1L)
				.firstName("Alice")
				.lastName("Smith")
				.birthDate(LocalDate.of(2016, 3, 15))
				.dressColor("Pink")
				.build();

		// when
		ChildDTO result = classUnderTest.mapToDTO(girl);

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

}

