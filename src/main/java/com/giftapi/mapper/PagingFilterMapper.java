package com.giftapi.mapper;

import com.giftapi.exception.GiftApiException;
import com.giftapi.model.entity.Child;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@UtilityClass
public class PagingFilterMapper {

	private static final String RANGE_SEPARATOR = ":";

	public static <T> Specification<T> fromFilters(Map<String, String> filters) {
		if (filters == null || filters.isEmpty()) {
			return Specification.unrestricted();
		}

		Specification<T> spec = Specification.unrestricted();
		for (Map.Entry<String, String> entry : filters.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();

			if ("page".equals(key) || "size".equals(key) || "sort".equals(key)) {
				continue;
			}

			if (value != null && !value.trim().isEmpty()) {
				if (value.contains(RANGE_SEPARATOR)) {
					spec = spec.and(handleRangeFilter(key, value));
				} else if ("type".equals(key)) {
					spec = spec.and(hasType(value));
				}
				else {
					spec = spec.and(attributeEquals(key, value));
				}
			}
		}
		return spec;
	}

	private static <T> Specification<T> attributeEquals(String attribute, Object value) {
		return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(attribute), value);
	}

	@SuppressWarnings("unchecked")
	private static <T> Specification<T> handleRangeFilter(String attribute, String value) {
		String[] parts = value.split(RANGE_SEPARATOR, 2);
		String from = parts.length > 0 ? parts[0].trim() : "";
		String to = parts.length > 1 ? parts[1].trim() : "";

		return (root, query, criteriaBuilder) -> {
			Path<?> path = root.get(attribute);
			Class<?> attributeType = path.getJavaType();

			Comparable fromValue = parseValue(from, attributeType);
			Comparable toValue = parseValue(to, attributeType);


			if (attributeType.equals(OffsetDateTime.class) && toValue instanceof OffsetDateTime toDateTime) {
				if (toDateTime.toLocalTime().equals(LocalTime.MIN)) {
					toValue = toDateTime.with(LocalTime.MAX);
				}
			}

			if (fromValue != null && toValue != null) {
				return criteriaBuilder.between(path.as(fromValue.getClass()), fromValue, toValue);
			}
			if (fromValue != null) {
				return criteriaBuilder.greaterThanOrEqualTo(path.as(fromValue.getClass()), fromValue);
			}
			if (toValue != null) {
				return criteriaBuilder.lessThanOrEqualTo(path.as(toValue.getClass()), toValue);
			}
			return criteriaBuilder.conjunction();

		};
	}

	private static Comparable<?> parseValue(String value, Class<?> type) {
		if (value == null || value.isEmpty()) {
			return null;
		}
		try {
			if (type.equals(Long.class) || type.equals(long.class)) {
				return Long.valueOf(value);
			}
			if (type.equals(Integer.class) || type.equals(int.class)) {
				return Integer.valueOf(value);
			}
			if (type.equals(LocalDate.class)) {
				return getOffsetDateTime(value);
			}
		} catch (NumberFormatException | DateTimeParseException e) {
			throw GiftApiException.of("Value: " + value + " is not valid for type: " + type.getSimpleName(), BAD_REQUEST);
		}
		return value;
	}

	private static OffsetDateTime getOffsetDateTime(String value) {
		try {
			LocalDate date = LocalDate.parse(value);
			return date.atStartOfDay().atOffset(ZoneOffset.UTC);
		} catch (DateTimeParseException e) {
			return OffsetDateTime.parse(value);
		}
	}

	private static <T> Specification<T> hasType(String type) {
		return (root, query, criteriaBuilder) ->
				criteriaBuilder.equal(root.type().as(String.class), type);
	}

}
