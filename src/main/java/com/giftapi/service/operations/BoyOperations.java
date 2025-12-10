package com.giftapi.service.operations;

import com.giftapi.mapper.BoyMapper;
import com.giftapi.model.dto.ChildDTO;
import com.giftapi.model.dto.command.child.CreateChildCommand;
import com.giftapi.model.dto.command.child.UpdateChildCommand;
import com.giftapi.model.entity.Child;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class BoyOperations implements ChildrenOperations {

	@Override
	public Child create(CreateChildCommand command) {
		Map<String, Object> properties = command.getProperties();
		return BoyMapper.toEntity(properties);
	}

	@Override
	public Child update(UpdateChildCommand command, Child child) {
		Map<String, Object> properties = command.getProperties();
		return BoyMapper.toEntity(child, properties);
	}

	@Override
	public ChildDTO mapToDTO(Child child) {
		return BoyMapper.toDTO(child);
	}
}
