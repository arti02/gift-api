package com.giftapi.dto.command.child;

import java.time.LocalDate;

public record UpdateChildCommand(String firstName, String lastName, LocalDate birthDate) {
}
