package org.springframework.samples.petclinic.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.samples.petclinic.model.User;

public interface UserService {
	
	boolean userExists(String username);

	int deleteUserList(Integer[] userIds);
}
