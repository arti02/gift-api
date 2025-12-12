package com.giftapi.service;

import com.giftapi.exception.GiftApiException;
import com.giftapi.mapper.PagingFilterMapper;
import com.giftapi.model.dto.ChildDTO;
import com.giftapi.model.dto.command.child.CreateChildCommand;
import com.giftapi.model.dto.command.child.UpdateChildCommand;
import com.giftapi.model.entity.Child;

import com.giftapi.repository.ChildRepository;
import com.giftapi.service.operations.ChildrenOperations;
import jakarta.persistence.DiscriminatorValue;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.giftapi.exception.GiftApiException.entityNotFound;
import static com.giftapi.service.operations.ChildrenOperations.OPERATIONS;
import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
public class ChildService {

	public static final String CHILD_BASE_CLASS = "child";
	private final ChildRepository childRepository;
	private final Map<String, ChildrenOperations> childrenOperations;

	public Page<ChildDTO> getAll(Map<String, String> filters, Pageable pageable) {
		Page<Child> page = childRepository.findAll(PagingFilterMapper.fromFilters(filters), pageable);
		if (page.isEmpty()) {
			return Page.empty(pageable);
		}

		return page.map(child -> {
			ChildDTO childDTO = getOperationForEntity(child).mapToDTO(child);
			childDTO.setGiftCount(child.getGiftCount());
			return childDTO;
		});
	}

	private ChildrenOperations getOperationForEntity(Child child) {
		String type;
		if (child.getClass().getSimpleName().equalsIgnoreCase(CHILD_BASE_CLASS)) {
			type = CHILD_BASE_CLASS;
		} else {
			type = child.getClass().getAnnotation(DiscriminatorValue.class).value().toLowerCase();
		}
		return childrenOperations.get(type + OPERATIONS);
	}

	public ChildDTO getById(Long id) {
		Child child = childRepository.findById(id).orElseThrow(() -> entityNotFound("Child not found with id: " + id));
		return getOperationForEntity(child).mapToDTO(child);
	}

	@Transactional
	public ChildDTO update(Long id, UpdateChildCommand cmd) {
		Child existing = childRepository.findWithLockingById(id).orElseThrow(() -> entityNotFound("Child not found with id: " + id));
		ChildrenOperations operation = getOperation(cmd.getProperties());
		Child child = operation.update(cmd, existing);
		return operation.mapToDTO(childRepository.save(child));
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
		ChildrenOperations operations = getOperation(cmd.getProperties());
		Child child = operations.create(cmd);
		return operations.mapToDTO(childRepository.save(child));
	}

	private ChildrenOperations getOperation(Map<String, Object> properties) {
		return ofNullable(childrenOperations.get(properties.get("type").toString().toLowerCase() + OPERATIONS)).orElseThrow(
				() -> GiftApiException.of("Unsupported child type: " + properties.get("type"), BAD_REQUEST));
	}

}
