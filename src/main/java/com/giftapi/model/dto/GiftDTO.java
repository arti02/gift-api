package com.giftapi.model.dto;

import java.math.BigDecimal;

public record GiftDTO(Long id, String name, BigDecimal price, Long childId) {

}
