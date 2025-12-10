package com.giftapi.mapper;

import com.giftapi.exception.GiftApiException;
import com.giftapi.model.dto.BoyDTO;
import com.giftapi.model.dto.ChildDTO;
import com.giftapi.model.dto.GirlDTO;
import com.giftapi.model.entity.Boy;
import com.giftapi.model.entity.Child;
import com.giftapi.model.entity.Girl;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@UtilityClass
public class GirlMapper {

	public static <T extends Child> ChildDTO toDTO(T child) {
		if (child instanceof Girl girl) {
			return new GirlDTO(
					girl.getId(),
					girl.getFirstName(),
					girl.getLastName(),
					girl.getBirthDate(),
					girl.getDressColor());
		}
		throw GiftApiException.of("Invalid type", BAD_REQUEST);
	}

	public static Child toEntity(Map<String, Object> properties) {
		return Girl.builder()
				.firstName(properties.get("firstName").toString())
				.lastName(properties.get("lastName").toString())
				.birthDate(LocalDate.parse(properties.get("birthDate").toString()))
				.dressColor(properties.get("dressColor").toString())
				.build();
	}

	public static Child toEntity(Child existing, Map<String, Object> properties) {
		if (existing instanceof Girl girl) {
			girl.setFirstName(properties.get("firstName").toString());
			girl.setLastName(properties.get("lastName").toString());
			girl.setBirthDate(LocalDate.parse(properties.get("birthDate").toString()));
			girl.setDressColor(properties.get("dressColor").toString());
			return girl;
		}
		throw GiftApiException.of("Invalid type", BAD_REQUEST);
	}
}

