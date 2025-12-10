package com.giftapi.service.operations;

import com.giftapi.mapper.GirlMapper;
import com.giftapi.model.dto.ChildDTO;
import com.giftapi.model.dto.command.child.CreateChildCommand;
import com.giftapi.model.dto.command.child.UpdateChildCommand;
import com.giftapi.model.entity.Child;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GirlOperations implements ChildrenOperations {

	@Override
	public Child create(CreateChildCommand command) {
		Map<String, Object> properties = command.getProperties();
		return GirlMapper.toEntity(properties);
	}

	@Override
	public Child update(UpdateChildCommand command, Child child) {
		Map<String, Object> properties = command.getProperties();
		return GirlMapper.toEntity(child, properties);
	}

	@Override
	public ChildDTO mapToDTO(Child child) {
		return GirlMapper.toDTO(child);
	}
}
