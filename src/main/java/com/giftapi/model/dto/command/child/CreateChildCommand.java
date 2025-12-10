package com.giftapi.model.dto.command.child;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
public class CreateChildCommand {

	private Map<String, Object> properties;

}
