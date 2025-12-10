package com.giftapi.mapper;

import com.giftapi.exception.GiftApiException;
import com.giftapi.model.dto.ChildDTO;
import com.giftapi.model.entity.Child;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@UtilityClass
public class ChildMapper {

	public static <T extends Child> ChildDTO toDTO(T child) {
		if (child instanceof Child ch) {
			return new ChildDTO(ch.getId(), ch.getFirstName(),
					ch.getLastName(), ch.getBirthDate(), "CHILD");
		}
		throw GiftApiException.of("Invalid type", BAD_REQUEST);
	}

	public static Child toEntity(Map<String, Object> properties) {
		return Child.builder()
				.firstName(properties.get("firstName").toString())
				.lastName(properties.get("lastName").toString())
				.birthDate(LocalDate.parse(properties.get("birthDate").toString()))
				.build();
	}
	public static Child toEntity(Child existing, Map<String, Object> properties) {
		existing.setFirstName(properties.get("firstName").toString());
		existing.setLastName(properties.get("lastName").toString());
		existing.setBirthDate(LocalDate.parse(properties.get("birthDate").toString()));
		return existing;
	}

}
