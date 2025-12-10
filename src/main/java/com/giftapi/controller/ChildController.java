package com.giftapi.controller;

import com.giftapi.model.dto.ChildDTO;
import com.giftapi.model.dto.GiftDTO;
import com.giftapi.model.dto.command.child.CreateChildCommand;
import com.giftapi.model.dto.command.child.UpdateChildCommand;
import com.giftapi.model.dto.command.gift.AddGiftCommand;
import com.giftapi.model.dto.command.gift.UpdateGiftCommand;
import com.giftapi.service.ChildService;
import com.giftapi.service.GiftService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/children")
@RequiredArgsConstructor
public class ChildController {

	private final ChildService childService;

	private final GiftService giftService;

	@GetMapping
	public Page<ChildDTO> getChildren(
			@PageableDefault Pageable pageable,
			@RequestParam(required = false) Map<String, String> filters) {
		return childService.getAll(filters, pageable);
	}

	@GetMapping("/{id}")
	public ChildDTO getById(@PathVariable Long id) {
		return childService.getById(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ChildDTO create(@Valid @RequestBody CreateChildCommand cmd) {
		return childService.create(cmd);
	}

	@PutMapping("/{id}")
	public ChildDTO update(@PathVariable Long id, @Valid @RequestBody UpdateChildCommand cmd) {
		return childService.update(id, cmd);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		childService.delete(id);
	}

	@GetMapping("{id}/gifts")
	public List<GiftDTO> getGiftsForChild(@PathVariable Long id) {
		return giftService.getGiftsForChild(id);
	}

	@GetMapping("{id}/gifts/{giftId}")
	public GiftDTO getGiftByIdForChild(@PathVariable Long id, @PathVariable Long giftId) {
		return giftService.getGiftByIdForChild(giftId, id);
	}

	@PostMapping("{id}/gifts")
	@ResponseStatus(HttpStatus.CREATED)
	public GiftDTO addGiftToChild(@PathVariable Long id, @Valid @RequestBody AddGiftCommand cmd) {
		return giftService.addGiftToChild(id, cmd);
	}

	@PutMapping("{id}/gifts/{giftId}")
	public GiftDTO updateGiftForChild(
			@PathVariable Long id,
			@PathVariable Long giftId,
			@Valid @RequestBody UpdateGiftCommand cmd) {
		return giftService.updateGiftForChild(giftId, id, cmd);
	}

	@DeleteMapping("{id}/gifts/{giftId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteGiftFromChild(@PathVariable Long id, @PathVariable Long giftId) {
		giftService.deleteGiftFromChild(giftId, id);
	}

}
