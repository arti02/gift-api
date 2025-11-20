package com.giftapi.controller;

import com.giftapi.dto.ChildDTO;
import com.giftapi.dto.command.child.CreateChildCommand;
import com.giftapi.dto.command.child.UpdateChildCommand;
import com.giftapi.service.ChildService;
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
@RequestMapping("/api/v1/child")
@RequiredArgsConstructor
public class ChildController {

	private final ChildService childService;

	@GetMapping
	public List<ChildDTO> getAll() {
		return childService.getAll();
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
	public ChildDTO update(@PathVariable Long id,
			@Valid @RequestBody UpdateChildCommand cmd) {
		return childService.update(id, cmd);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		childService.delete(id);
	}



}
