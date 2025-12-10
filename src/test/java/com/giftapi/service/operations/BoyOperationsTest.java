package com.giftapi.service.operations;

import com.giftapi.model.dto.BoyDTO;
import com.giftapi.model.dto.ChildDTO;
import com.giftapi.model.dto.command.child.CreateChildCommand;
import com.giftapi.model.dto.command.child.UpdateChildCommand;
import com.giftapi.model.entity.Boy;
import com.giftapi.model.entity.Child;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BoyOperationsTest {

	@InjectMocks
	private BoyOperations classUnderTest;

	@Test
	void create_shouldCreateBoyFromCommand() {
		// given
		Map<String, Object> properties = new HashMap<>();
		properties.put("firstName", "John");
		properties.put("lastName", "Doe");
		properties.put("birthDate", "2015-05-10");
		properties.put("favoriteSport", "Football");

		CreateChildCommand command = new CreateChildCommand(properties);

		// when
		Child result = classUnderTest.create(command);

		// then
		assertThat(result).isInstanceOf(Boy.class);
		Boy boy = (Boy) result;
		assertThat(boy.getFirstName()).isEqualTo("John");
		assertThat(boy.getLastName()).isEqualTo("Doe");
		assertThat(boy.getBirthDate()).isEqualTo(LocalDate.of(2015, 5, 10));
		assertThat(boy.getFavoriteSport()).isEqualTo("Football");
	}

	@Test
	void update_shouldUpdateBoyFromCommand() {
		// given
		Boy existingBoy = Boy.builder()
				.id(1L)
				.firstName("John")
				.lastName("Doe")
				.birthDate(LocalDate.of(2015, 5, 10))
				.favoriteSport("Football")
				.build();

		Map<String, Object> properties = new HashMap<>();
		properties.put("firstName", "Johnny");
		properties.put("lastName", "Doe");
		properties.put("birthDate", "2015-05-10");
		properties.put("favoriteSport", "Basketball");

		UpdateChildCommand command = new UpdateChildCommand(properties);

		// when
		Child result = classUnderTest.update(command, existingBoy);

		// then
		assertThat(result).isInstanceOf(Boy.class);
		Boy boy = (Boy) result;
		assertThat(boy.getId()).isEqualTo(1L); // ID should be preserved
		assertThat(boy.getFirstName()).isEqualTo("Johnny");
		assertThat(boy.getLastName()).isEqualTo("Doe");
		assertThat(boy.getFavoriteSport()).isEqualTo("Basketball");
	}

	@Test
	void mapToDTO_shouldMapBoyToDTO() {
		// given
		Boy boy = Boy.builder()
				.id(1L)
				.firstName("John")
				.lastName("Doe")
				.birthDate(LocalDate.of(2015, 5, 10))
				.favoriteSport("Football")
				.build();

		// when
		ChildDTO result = classUnderTest.mapToDTO(boy);

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

}

