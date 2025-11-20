package com.giftapi.mapper;

import com.giftapi.dto.ChildDTO;
import com.giftapi.model.Child;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ChildMapper {

	public static ChildDTO toDto(Child lesson) {
		return new ChildDTO(lesson.getId(), lesson.getFirstName(), lesson.getLastName(), lesson.getBirthDate());
	}

	public static Child toEntity(ChildDTO childDTO) {
		Child child = new Child();
		child.setFirstName(childDTO.firstName());
		child.setLastName(childDTO.lastName());
		child.setBirthDate(childDTO.birthDate());
		return child;
	}

}
