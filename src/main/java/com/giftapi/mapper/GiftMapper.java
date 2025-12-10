package com.giftapi.mapper;

import com.giftapi.model.dto.GiftDTO;
import com.giftapi.model.entity.Gift;
import lombok.experimental.UtilityClass;

@UtilityClass
public class GiftMapper {

	public static GiftDTO toDto(Gift gift) {
		return new GiftDTO(gift.getId(), gift.getName(), gift.getPrice(), gift.getChild().getId());
	}
}
