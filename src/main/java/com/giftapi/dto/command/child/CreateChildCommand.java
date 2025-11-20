package com.giftapi.dto.command.child;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateChildCommand(
		@NotNull(message = "New child name must be provided") String firstName,
		@NotNull(message = "New child last name must be provided") String lastName,
		@NotNull(message = "New child birth date must be provided") LocalDate birthDate) {
}
