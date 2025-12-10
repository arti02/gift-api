package com.giftapi.model.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Setter;
import lombok.Getter;
import jakarta.persistence.Entity;
import jakarta.persistence.DiscriminatorValue;
import lombok.experimental.SuperBuilder;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@DiscriminatorValue("BOY")
public class Boy extends Child {

	@ToString.Include
	private String favoriteSport;

}