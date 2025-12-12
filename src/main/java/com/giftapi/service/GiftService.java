package com.giftapi.service;

import com.giftapi.model.dto.GiftDTO;
import com.giftapi.model.dto.command.gift.AddGiftCommand;
import com.giftapi.model.dto.command.gift.UpdateGiftCommand;
import com.giftapi.exception.GiftApiException;
import com.giftapi.mapper.GiftMapper;
import com.giftapi.model.entity.Child;
import com.giftapi.model.entity.Gift;
import com.giftapi.repository.ChildRepository;
import com.giftapi.repository.GiftRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.giftapi.exception.GiftApiException.entityNotFound;

@Service
@RequiredArgsConstructor
public class GiftService {

	private final ChildRepository childRepository;
	private final GiftRepository giftRepository;

	public List<GiftDTO> getGiftsForChild(Long childId) {
		Child child = childRepository.findById(childId)
				.orElseThrow(() -> entityNotFound("Child not found with id: " + childId));
		return child.getGifts().stream().map(GiftMapper::toDto).toList();
	}

	@Transactional
	public GiftDTO addGiftToChild(Long childId, AddGiftCommand cmd) {
		Child child = childRepository.findWithLockingById(childId)
				.orElseThrow(() -> entityNotFound("Child not found with id: " + childId));
		if (child.getGifts().size() >= 3) {
			throw GiftApiException.of("Child can have max 3 gifts", HttpStatus.BAD_REQUEST);
		}
		Gift gift = new Gift();
		gift.setName(cmd.name());
		gift.setPrice(cmd.price());
		gift.setChild(child);
		child.getGifts().add(gift);
		return GiftMapper.toDto(giftRepository.save(gift));
	}

	@Transactional
	public void deleteGiftFromChild(Long giftId, Long childId) {
		Child child = childRepository.findWithLockingById(childId)
				.orElseThrow(() -> entityNotFound("Child not found with id: " + childId));
		child.getGifts().removeIf(gift -> gift.getId().equals(giftId));
	}

	public GiftDTO getGiftByIdForChild(Long giftId, Long childId) {
		return giftRepository.findByIdAndChildId(giftId, childId)
				.map(GiftMapper::toDto)
				.orElseThrow(() -> entityNotFound("Gift with id " + giftId + " for child " + childId + " not found"));
	}

	@Transactional
	public GiftDTO updateGiftForChild(Long giftId, Long childId,  UpdateGiftCommand cmd) {
		Child child = childRepository.findWithLockingById(childId)
				.orElseThrow(() -> entityNotFound("Child not found with id: " + childId));

		Gift gift = giftRepository.findByIdAndChildId(giftId, child.getId())
				.orElseThrow(() -> entityNotFound("Gift with id " + giftId + " for child " + childId + " not found"));

		gift.setName(cmd.name());
		gift.setPrice(cmd.price());
		Gift updated = giftRepository.save(gift);

		return GiftMapper.toDto(updated);
	}

}
