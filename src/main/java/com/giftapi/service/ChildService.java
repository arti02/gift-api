package com.giftapi.service;

import com.giftapi.dto.ChildDTO;
import com.giftapi.dto.command.child.CreateChildCommand;
import com.giftapi.dto.command.child.UpdateChildCommand;
import com.giftapi.mapper.ChildMapper;
import com.giftapi.model.Child;
import com.giftapi.repository.ChildRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.giftapi.exception.GiftApiException.entityNotFound;

@Service
@RequiredArgsConstructor
public class ChildService {

	private final ChildRepository childRepository;

	public List<ChildDTO> getAll() {
		return childRepository.findAll().stream().map(ChildMapper::toDto).toList();
	}

	public ChildDTO getById(Long id) {
		Child child = childRepository.findById(id).orElseThrow(() -> entityNotFound("Child not found with id: " + id));
		return ChildMapper.toDto(child);
	}

	@Transactional
	public ChildDTO update(Long id, UpdateChildCommand cmd) {
		Child existing = childRepository.findById(id).orElseThrow(() -> entityNotFound("Child not found with id: " + id));
		existing.setFirstName(cmd.firstName());
		existing.setLastName(cmd.lastName());
		existing.setBirthDate(cmd.birthDate());
		return ChildMapper.toDto(existing);
	}

	@Transactional
	public void delete(Long id) {
		if (!childRepository.existsById(id)) {
			throw entityNotFound("Child not found with id: " + id);
		}
		childRepository.deleteById(id);
	}

	@Transactional
	public ChildDTO create(CreateChildCommand cmd) {
		Child entity = new Child();
		entity.setFirstName(cmd.firstName());
		entity.setLastName(cmd.lastName());
		entity.setBirthDate(cmd.birthDate());
		Child saved = childRepository.save(entity);
		return ChildMapper.toDto(saved);
	}

}
