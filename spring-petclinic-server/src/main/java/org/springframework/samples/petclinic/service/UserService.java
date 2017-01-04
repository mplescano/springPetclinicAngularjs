package org.springframework.samples.petclinic.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.samples.petclinic.dto.form.UserForm;
import org.springframework.samples.petclinic.dto.form.UserQueryForm;
import org.springframework.samples.petclinic.model.User;

public interface UserService {
	
	User save(UserForm user);

	boolean userExists(String username);

	Page<User> findUserList(UserQueryForm userQueryForm, Pageable pageable);
}
