package org.springframework.samples.petclinic.service;

import org.springframework.samples.petclinic.model.User;

public interface UserService {
	
	User save(User user);

	boolean userExists(String username);
}
