package com.giftapi.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.giftapi.model.entity.Boy;
import com.giftapi.model.entity.Child;
import com.giftapi.model.entity.Gift;
import com.giftapi.model.entity.Girl;

import java.math.BigDecimal;
import java.time.LocalDate;

public class GiftApiTestHelper {

	private static final ObjectMapper objectMapper = new ObjectMapper()
			.registerModule(new JavaTimeModule());

	public Boy createBoy(String firstName, String lastName) {
		Boy boy = new Boy();
		boy.setFirstName(firstName);
		boy.setLastName(lastName);
		boy.setBirthDate(LocalDate.of(2015, 5, 10));
		boy.setFavoriteSport("Football");
		return boy;
	}

	public Boy createBoyWithId(long id, String firstName, String lastName) {
		Boy boy = createBoy(firstName, lastName);
		boy.setId(id);
		return boy;
	}

	public Girl createGirl(String firstName, String lastName) {
		Girl girl = new Girl();
		girl.setFirstName(firstName);
		girl.setLastName(lastName);
		girl.setBirthDate(LocalDate.of(2016, 3, 15));
		girl.setDressColor("Pink");
		return girl;
	}

	public Girl createGirlWithId(long id, String firstName, String lastName) {
		Girl girl = createGirl(firstName, lastName);
		girl.setId(id);
		return girl;
	}

	public Gift createGift(String name, BigDecimal price, Child child) {
		Gift gift = new Gift();
		gift.setName(name);
		gift.setPrice(price);
		gift.setChild(child);
		return gift;
	}

	public Gift createGiftWithId(Long id, String name, BigDecimal price, Child child) {
		Gift gift = createGift(name, price, child);
		gift.setId(id);
		return gift;
	}

	public String toJson(Object obj) {
		try {
			return objectMapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Failed to serialize object to JSON", e);
		}
	}
}
