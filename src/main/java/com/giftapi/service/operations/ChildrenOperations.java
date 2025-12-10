package com.giftapi.service.operations;

import com.giftapi.model.dto.ChildDTO;
import com.giftapi.model.dto.command.child.CreateChildCommand;
import com.giftapi.model.dto.command.child.UpdateChildCommand;
import com.giftapi.model.entity.Child;

public interface ChildrenOperations {

	String OPERATIONS = "Operations";

	Child create(CreateChildCommand command);

	Child update(UpdateChildCommand command, Child child);

	ChildDTO mapToDTO(Child child);

}
