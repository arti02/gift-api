package com.giftapi.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
public class ChildDTO {

	private Long id;
	private String firstName;
	private String lastName;
	private LocalDate birthDate;
	private String type;
	private Long giftCount;

	public ChildDTO(Long id, String firstName, String lastName, LocalDate birthDate, String type) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.birthDate = birthDate;
		this.type = type;
	}
}
