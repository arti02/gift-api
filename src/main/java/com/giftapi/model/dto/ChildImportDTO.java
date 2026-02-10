package com.giftapi.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.UtilityClass;

import java.sql.Date;

@Data
@AllArgsConstructor
public class ChildImportDTO {

	private String firstName;
	private String lastName;
	private Date birthDate;
}
