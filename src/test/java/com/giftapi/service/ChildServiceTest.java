package com.giftapi.service;

import com.giftapi.common.GiftApiTestHelper;
import com.giftapi.exception.GiftApiException;
import com.giftapi.model.dto.BoyDTO;
import com.giftapi.model.dto.ChildDTO;
import com.giftapi.model.dto.command.child.CreateChildCommand;
import com.giftapi.model.dto.command.child.UpdateChildCommand;
import com.giftapi.model.entity.Child;
import com.giftapi.model.entity.Boy;
import com.giftapi.repository.ChildRepository;
import com.giftapi.service.operations.ChildrenOperations;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChildServiceTest extends GiftApiTestHelper {

	@Mock
	private ChildRepository childRepository;

	@Mock
	private Map<String, ChildrenOperations> childrenOperations;

	@Mock
	private ChildrenOperations boyOperations;

	@InjectMocks
	private ChildService classUnderTest;

	@Captor
	private ArgumentCaptor<Child> childCaptor;

	@Test
	void getAll_shouldReturnPageWithGiftCounts() {
		// given
		long childId1 = 1L;
		long childId2 = 2L;
		Boy child1 = createBoyWithId(childId1, "John", "Doe");
		child1.setGiftCount(5L);
		Boy child2 = createBoyWithId(childId2, "Jane", "Smith");
		child2.setGiftCount(3L);
		List<Child> children = List.of(child1, child2);
		Pageable pageable = PageRequest.of(0, 10);
		Page<Child> page = new PageImpl<>(children, pageable, children.size());
		ChildDTO dto1 = new ChildDTO(childId1, "John", "Doe", LocalDate.now(), "CHILD");
		ChildDTO dto2 = new ChildDTO(childId2, "Jane", "Smith", LocalDate.now(), "CHILD");

		when(childRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
		when(childrenOperations.get("boyOperations")).thenReturn(boyOperations);
		when(boyOperations.mapToDTO(child1)).thenReturn(dto1);
		when(boyOperations.mapToDTO(child2)).thenReturn(dto2);

		// when
		Page<ChildDTO> result = classUnderTest.getAll(Map.of(), pageable);

		// then
		verify(childRepository).findAll(any(Specification.class), eq(pageable));

		assertThat(result.getContent()).hasSize(2);
		assertThat(result.getContent().get(0).getGiftCount()).isEqualTo(5L);
		assertThat(result.getContent().get(1).getGiftCount()).isEqualTo(3L);
	}

	@Test
	void getById_shouldReturnChildDto() {
		// given
		long childId = 1L;
		Boy child = createBoyWithId(childId, "John", "Doe");
		ChildDTO expectedDto = new ChildDTO(childId, "John", "Doe", LocalDate.now(), "BOY");

		when(childRepository.findById(childId)).thenReturn(Optional.of(child));
		when(childrenOperations.get("boyOperations")).thenReturn(boyOperations);
		when(boyOperations.mapToDTO(child)).thenReturn(expectedDto);

		// when
		ChildDTO result = classUnderTest.getById(childId);

		// then
		verify(childRepository).findById(childId);
		verify(boyOperations).mapToDTO(child);
		assertThat(result).isEqualTo(expectedDto);
	}

	@Test
	void getById_shouldThrowException_whenChildNotFound() {
		// given
		long childId = 999L;
		when(childRepository.findById(childId)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> classUnderTest.getById(childId)).isInstanceOf(GiftApiException.class);

		verify(childRepository).findById(childId);
	}

	@Test
	void create_shouldSaveAndReturnDto() {
		// given
		long expectedId = 10L;
		Map<String, Object> properties = Map.of(
				"firstName",
				"John",
				"lastName",
				"Doe",
				"birthDate",
				"2015-05-20",
				"type",
				"boy",
				"favoriteSport",
				"Soccer");
		CreateChildCommand cmd = new CreateChildCommand(properties);
		Boy child = createBoy("John", "Doe");
		ChildDTO expectedDto = new ChildDTO(expectedId, "John", "Doe", LocalDate.of(2015, 5, 10), "BOY");

		when(childrenOperations.get("boyOperations")).thenReturn(boyOperations);
		when(boyOperations.create(cmd)).thenReturn(child);
		when(childRepository.save(any(Child.class))).thenAnswer(args -> {
			Child saved = args.getArgument(0);
			saved.setId(expectedId);
			return saved;
		});
		when(boyOperations.mapToDTO(any(Child.class))).thenReturn(expectedDto);

		// when
		ChildDTO result = classUnderTest.create(cmd);

		// then
		verify(boyOperations).create(cmd);
		verify(childRepository).save(childCaptor.capture());
		Child saved = childCaptor.getValue();
		assertThat(saved.getFirstName()).isEqualTo("John");
		assertThat(saved.getLastName()).isEqualTo("Doe");

		assertThat(result.getId()).isEqualTo(expectedId);
		assertThat(result.getFirstName()).isEqualTo("John");
		assertThat(result.getLastName()).isEqualTo("Doe");
	}

	@Test
	void update_shouldUpdateAndReturnDto() {
		// given
		long childId = 1L;
		Map<String, Object> properties = Map.of(
				"firstName",
				"John",
				"lastName",
				"Doe",
				"birthDate",
				"2015-05-20",
				"type",
				"boy",
				"favoriteSport",
				"Soccer");
		UpdateChildCommand cmd = new UpdateChildCommand(properties);
		Boy existingChild = createBoyWithId(childId, "John", "Doe");
		BoyDTO expectedDto = new BoyDTO(childId, "Johnny", "Doe", LocalDate.of(2015, 5, 10), "Football");

		when(childRepository.findById(childId)).thenReturn(Optional.of(existingChild));
		when(childrenOperations.get("boyOperations")).thenReturn(boyOperations);
		when(boyOperations.update(cmd, existingChild)).thenReturn(existingChild);
		when(childRepository.save(existingChild)).thenReturn(existingChild);
		when(boyOperations.mapToDTO(existingChild)).thenReturn(expectedDto);

		// when
		ChildDTO result = classUnderTest.update(childId, cmd);

		// then
		verify(childRepository).findById(childId);
		verify(boyOperations).update(cmd, existingChild);
		assertThat(result.getFirstName()).isEqualTo("Johnny");
	}

	@Test
	void delete_shouldDeleteChild() {
		// given
		long childId = 1L;
		when(childRepository.existsById(childId)).thenReturn(true);
		doNothing().when(childRepository).deleteById(childId);

		// when
		classUnderTest.delete(childId);

		// then
		verify(childRepository).existsById(childId);
		verify(childRepository).deleteById(childId);
	}

	@Test
	void delete_shouldThrowException_whenChildNotFound() {
		// given
		long childId = 999L;
		when(childRepository.existsById(childId)).thenReturn(false);

		// when & then
		assertThatThrownBy(() -> classUnderTest.delete(childId)).isInstanceOf(GiftApiException.class);

		verify(childRepository).existsById(childId);
		verify(childRepository, never()).deleteById(childId);
	}

}