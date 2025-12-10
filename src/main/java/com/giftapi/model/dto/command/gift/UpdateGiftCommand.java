package com.giftapi.model.dto.command.gift;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateGiftCommand(
		@NotNull String name, @NotNull BigDecimal price) {

}