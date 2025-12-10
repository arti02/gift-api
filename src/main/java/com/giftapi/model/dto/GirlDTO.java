package com.giftapi.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class GirlDTO extends ChildDTO {

	private String dressColor;

	public GirlDTO(Long id, String firstName, String lastName, LocalDate birthDate, String dressColor) {
		super(id, firstName, lastName, birthDate, "GIRL");
		this.dressColor = dressColor;
	}

}

