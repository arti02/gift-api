package com.giftapi.service;

import com.giftapi.common.GiftApiTestHelper;
import com.giftapi.exception.GiftApiException;
import com.giftapi.model.dto.GiftDTO;
import com.giftapi.model.dto.command.gift.AddGiftCommand;
import com.giftapi.model.dto.command.gift.UpdateGiftCommand;
import com.giftapi.model.entity.Boy;
import com.giftapi.model.entity.Child;
import com.giftapi.model.entity.Gift;
import com.giftapi.repository.ChildRepository;
import com.giftapi.repository.GiftRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GiftServiceTest extends GiftApiTestHelper {

	@Mock
	private ChildRepository childRepository;

	@Mock
	private GiftRepository giftRepository;

	@InjectMocks
	private GiftService classUnderTest;

	@Captor
	private ArgumentCaptor<Gift> giftCaptor;

	@Test
	void getGiftsForChild_shouldReturnListOfGifts() {
		// given
		long childId = 1L;
		Boy child = createBoyWithId(childId, "John", "Doe");
		Gift gift1 = createGiftWithId(1L, "Toy Car", BigDecimal.valueOf(29.99), child);
		Gift gift2 = createGiftWithId(2L, "Lego Set", BigDecimal.valueOf(49.99), child);
		child.setGifts(Set.of(gift1, gift2));

		when(childRepository.findById(childId)).thenReturn(Optional.of(child));

		// when
		List<GiftDTO> result = classUnderTest.getGiftsForChild(childId);

		// then
		verify(childRepository).findById(childId);
		assertThat(result).hasSize(2);
		assertThat(result).extracting(GiftDTO::name).containsExactlyInAnyOrder("Toy Car", "Lego Set");
		assertThat(result).extracting(GiftDTO::price)
				.containsExactlyInAnyOrder(BigDecimal.valueOf(29.99), BigDecimal.valueOf(49.99));
	}

	@Test
	void getGiftsForChild_shouldThrowException_whenChildNotFound() {
		// given
		long childId = 999L;
		when(childRepository.findById(childId)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> classUnderTest.getGiftsForChild(childId))
				.isInstanceOf(GiftApiException.class);

		verify(childRepository).findById(childId);
	}

	@Test
	void addGiftToChild_shouldSaveAndReturnDto() {
		// given
		long childId = 1L;
		long expectedGiftId = 10L;
		AddGiftCommand cmd = new AddGiftCommand("Toy Car", BigDecimal.valueOf(29.99));
		Boy child = createBoyWithId(childId, "John", "Doe");
		child.setGifts(new HashSet<>());

		when(childRepository.findWithLockingById(childId)).thenReturn(Optional.of(child));
		when(giftRepository.countGiftByChildId(childId)).thenReturn(0L);
		when(giftRepository.save(any(Gift.class))).thenAnswer(args -> {
			Gift gift = args.getArgument(0);
			gift.setId(expectedGiftId);
			return gift;
		});

		// when
		GiftDTO result = classUnderTest.addGiftToChild(childId, cmd);

		// then
		verify(childRepository).findWithLockingById(childId);
		verify(giftRepository).countGiftByChildId(childId);
		verify(giftRepository).save(giftCaptor.capture());

		Gift saved = giftCaptor.getValue();
		assertThat(saved.getName()).isEqualTo("Toy Car");
		assertThat(saved.getPrice()).isEqualTo(BigDecimal.valueOf(29.99));
		assertThat(saved.getChild()).isEqualTo(child);

		assertThat(result.id()).isEqualTo(expectedGiftId);
		assertThat(result.name()).isEqualTo("Toy Car");
		assertThat(result.price()).isEqualTo(BigDecimal.valueOf(29.99));
		assertThat(result.childId()).isEqualTo(childId);
	}

	@Test
	void addGiftToChild_shouldThrowException_whenChildNotFound() {
		// given
		long childId = 999L;
		AddGiftCommand cmd = new AddGiftCommand("Toy Car", BigDecimal.valueOf(29.99));

		when(childRepository.findWithLockingById(childId)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> classUnderTest.addGiftToChild(childId, cmd))
				.isInstanceOf(GiftApiException.class);

		verify(childRepository).findWithLockingById(childId);
		verify(giftRepository, never()).save(any(Gift.class));
	}

	@Test
	void addGiftToChild_shouldThrowException_whenChildHasMaxGifts() {
		// given
		long childId = 1L;
		AddGiftCommand cmd = new AddGiftCommand("Toy Car", BigDecimal.valueOf(29.99));
		Boy child = createBoyWithId(childId, "John", "Doe");

		when(childRepository.findWithLockingById(childId)).thenReturn(Optional.of(child));
		when(giftRepository.countGiftByChildId(childId)).thenReturn(3L);

		// when & then
		assertThatThrownBy(() -> classUnderTest.addGiftToChild(childId, cmd))
				.isInstanceOf(GiftApiException.class);

		verify(childRepository).findWithLockingById(childId);
		verify(giftRepository).countGiftByChildId(childId);
		verify(giftRepository, never()).save(any(Gift.class));
	}

	@Test
	void getGiftByIdForChild_shouldReturnGiftDto() {
		// given
		long giftId = 1L;
		long childId = 1L;
		Boy child = createBoyWithId(childId, "John", "Doe");
		Gift gift = createGiftWithId(giftId, "Toy Car", BigDecimal.valueOf(29.99), child);

		when(giftRepository.findByIdAndChildId(giftId, childId)).thenReturn(Optional.of(gift));

		// when
		GiftDTO result = classUnderTest.getGiftByIdForChild(giftId, childId);

		// then
		verify(giftRepository).findByIdAndChildId(giftId, childId);
		assertThat(result.id()).isEqualTo(giftId);
		assertThat(result.name()).isEqualTo("Toy Car");
		assertThat(result.price()).isEqualTo(BigDecimal.valueOf(29.99));
		assertThat(result.childId()).isEqualTo(childId);
	}

	@Test
	void getGiftByIdForChild_shouldThrowException_whenGiftNotFound() {
		// given
		long giftId = 999L;
		long childId = 1L;

		when(giftRepository.findByIdAndChildId(giftId, childId)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> classUnderTest.getGiftByIdForChild(giftId, childId))
				.isInstanceOf(GiftApiException.class);

		verify(giftRepository).findByIdAndChildId(giftId, childId);
	}

	@Test
	void updateGiftForChild_shouldUpdateAndReturnDto() {
		// given
		long giftId = 1L;
		long childId = 1L;
		UpdateGiftCommand cmd = new UpdateGiftCommand("Updated Toy", BigDecimal.valueOf(39.99));
		Boy child = createBoyWithId(childId, "John", "Doe");
		Gift gift = createGiftWithId(giftId, "Toy Car", BigDecimal.valueOf(29.99), child);

		when(childRepository.findWithLockingById(childId)).thenReturn(Optional.of(child));
		when(giftRepository.findByIdAndChildId(giftId, childId)).thenReturn(Optional.of(gift));
		when(giftRepository.save(any(Gift.class))).thenAnswer(args -> args.getArgument(0));

		// when
		GiftDTO result = classUnderTest.updateGiftForChild(giftId, childId, cmd);

		// then
		verify(childRepository).findWithLockingById(childId);
		verify(giftRepository).findByIdAndChildId(giftId, childId);
		verify(giftRepository).save(giftCaptor.capture());

		Gift updated = giftCaptor.getValue();
		assertThat(updated.getName()).isEqualTo("Updated Toy");
		assertThat(updated.getPrice()).isEqualTo(BigDecimal.valueOf(39.99));

		assertThat(result.name()).isEqualTo("Updated Toy");
		assertThat(result.price()).isEqualTo(BigDecimal.valueOf(39.99));
	}

	@Test
	void updateGiftForChild_shouldThrowException_whenChildNotFound() {
		// given
		long giftId = 1L;
		long childId = 999L;
		UpdateGiftCommand cmd = new UpdateGiftCommand("Updated Toy", BigDecimal.valueOf(39.99));

		when(childRepository.findWithLockingById(childId)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> classUnderTest.updateGiftForChild(giftId, childId, cmd))
				.isInstanceOf(GiftApiException.class);

		verify(childRepository).findWithLockingById(childId);
		verify(giftRepository, never()).save(any(Gift.class));
	}

	@Test
	void updateGiftForChild_shouldThrowException_whenGiftNotFound() {
		// given
		long giftId = 999L;
		long childId = 1L;
		UpdateGiftCommand cmd = new UpdateGiftCommand("Updated Toy", BigDecimal.valueOf(39.99));
		Boy child = createBoyWithId(childId, "John", "Doe");

		when(childRepository.findWithLockingById(childId)).thenReturn(Optional.of(child));
		when(giftRepository.findByIdAndChildId(giftId, childId)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> classUnderTest.updateGiftForChild(giftId, childId, cmd))
				.isInstanceOf(GiftApiException.class);

		verify(childRepository).findWithLockingById(childId);
		verify(giftRepository).findByIdAndChildId(giftId, childId);
		verify(giftRepository, never()).save(any(Gift.class));
	}

	@Test
	void deleteGiftFromChild_shouldRemoveGiftFromChild() {
		// given
		long giftId = 1L;
		long childId = 1L;
		Boy child = createBoyWithId(childId, "John", "Doe");
		Gift gift = createGiftWithId(giftId, "Toy Car", BigDecimal.valueOf(29.99), child);
		child.setGifts(new HashSet<>(Set.of(gift)));

		when(childRepository.findWithLockingById(childId)).thenReturn(Optional.of(child));

		// when
		classUnderTest.deleteGiftFromChild(giftId, childId);

		// then
		verify(childRepository).findWithLockingById(childId);
		assertThat(child.getGifts()).isEmpty();
	}

	@Test
	void deleteGiftFromChild_shouldThrowException_whenChildNotFound() {
		// given
		long giftId = 1L;
		long childId = 999L;

		when(childRepository.findWithLockingById(childId)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> classUnderTest.deleteGiftFromChild(giftId, childId))
				.isInstanceOf(GiftApiException.class);

		verify(childRepository).findWithLockingById(childId);
	}
}

