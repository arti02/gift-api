package com.giftapi.model.dto.command.child;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class UpdateChildCommand {

	private Map<String, Object> properties;
}
