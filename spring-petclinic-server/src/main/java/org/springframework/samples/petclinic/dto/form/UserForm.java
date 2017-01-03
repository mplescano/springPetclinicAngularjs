package org.springframework.samples.petclinic.dto.form;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * 
 * TODO add catpcha functionality, validation too
 * 
 * @author mplescano
 *
 */
//@NotRepeatedUser
public class UserForm {

	@NotEmpty
	private String firstName;
	
	@NotEmpty
	private String lastName;
	
	@NotEmpty
	private String username;
	
	//@PasswordEquals
	@NotEmpty
	private String password;
	
	@NotEmpty
	private String passwordAgain;
	
	@NotEmpty
	private String roles;
	
	
}
