package com.giftapi.controller;

import com.giftapi.dto.GiftDTO;
import com.giftapi.dto.command.gift.AddGiftCommand;
import com.giftapi.dto.command.gift.UpdateGiftCommand;
import com.giftapi.service.GiftService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/child/{childId}/gift")
@RequiredArgsConstructor
public class GiftController {

	private final GiftService giftService;

	@GetMapping
	public List<GiftDTO> getGiftsForChild(@PathVariable Long childId) {
		return giftService.getGiftsForChild(childId);
	}

	@GetMapping("/{giftId}")
	public GiftDTO getGiftByIdForChild(@PathVariable Long childId, @PathVariable Long giftId) {
		return giftService.getGiftByIdForChild(giftId, childId);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public GiftDTO addGiftToChild(@PathVariable Long childId, @Valid @RequestBody AddGiftCommand cmd) {
		return giftService.addGiftToChild(childId, cmd);
	}

	@PutMapping("/{giftId}")
	public GiftDTO updateGiftForChild(@PathVariable Long childId, @PathVariable Long giftId, @Valid @RequestBody UpdateGiftCommand cmd) {
		return giftService.updateGiftForChild(giftId, childId, cmd);
	}

	@DeleteMapping("/{giftId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteGiftFromChild(@PathVariable Long childId, @PathVariable Long giftId) {
		giftService.deleteGiftFromChild(giftId, childId);
	}

}

