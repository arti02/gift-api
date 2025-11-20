package com.giftapi.dto;

import java.time.LocalDate;

public record ChildDTO(Long id, String firstName, String lastName, LocalDate birthDate) {

}
