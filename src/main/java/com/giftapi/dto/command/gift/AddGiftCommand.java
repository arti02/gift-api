package com.giftapi.dto.command.gift;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AddGiftCommand(
		@NotNull(message = "Present name should be present") String name,
		@NotNull(message = "Price should be provided") BigDecimal price) {
}
