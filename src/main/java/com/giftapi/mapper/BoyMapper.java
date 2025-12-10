package com.giftapi.mapper;

import com.giftapi.exception.GiftApiException;
import com.giftapi.model.entity.Child;
import com.giftapi.model.entity.Boy;
import com.giftapi.model.dto.ChildDTO;
import com.giftapi.model.dto.BoyDTO;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@UtilityClass
public class BoyMapper {

	public static <T extends Child> ChildDTO toDTO(T child) {
		if (child instanceof Boy boy) {
			return new BoyDTO(
					boy.getId(),
					boy.getFirstName(),
					boy.getLastName(),
					boy.getBirthDate(),
					boy.getFavoriteSport()
			);
		}
		throw GiftApiException.of("Invalid type", BAD_REQUEST);
	}

	public static Child toEntity(Map<String, Object> properties) {
		return Boy.builder()
				.firstName(properties.get("firstName").toString())
				.lastName(properties.get("lastName").toString())
				.birthDate(LocalDate.parse(properties.get("birthDate").toString()))
				.favoriteSport(properties.get("favoriteSport").toString())
				.build();
	}

	public static Child toEntity(Child child, Map<String, Object> properties) {
		if (child instanceof Boy boy) {
			boy.setFirstName(properties.get("firstName").toString());
			boy.setLastName(properties.get("lastName").toString());
			boy.setBirthDate(LocalDate.parse(properties.get("birthDate").toString()));
			boy.setFavoriteSport(properties.get("favoriteSport").toString());
			return boy;
		}
		throw GiftApiException.of("Invalid type", BAD_REQUEST);
	}
}