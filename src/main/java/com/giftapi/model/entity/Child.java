package com.giftapi.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.SecondaryTable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Immutable;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "child_type", discriminatorType = DiscriminatorType.STRING)
@SecondaryTable(
		name = "child_gift_count_view",
		pkJoinColumns = @PrimaryKeyJoinColumn(name = "child_id", referencedColumnName = "id")
)
public class Child {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long id;

	@ToString.Include
	private String firstName;

	@ToString.Include
	private String lastName;

	@ToString.Include
	private LocalDate birthDate;

	@OneToMany(mappedBy = "child", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Gift> gifts = new HashSet<>();

	@Immutable
	@Column(table = "child_gift_count_view", name = "gift_count", insertable = false, updatable = false)
	private Long giftCount;

}
