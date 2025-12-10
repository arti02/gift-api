package com.giftapi.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class BoyDTO extends ChildDTO {

	private String favoriteSport;

	public BoyDTO(Long id, String firstName, String lastName, LocalDate birthDate, String favoriteSport) {
		super(id, firstName, lastName, birthDate, "BOY");
		this.favoriteSport = favoriteSport;
	}

}