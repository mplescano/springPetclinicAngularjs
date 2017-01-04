package org.springframework.samples.petclinic.dto.form;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.samples.petclinic.constraint.UniqueUsername;
import org.springframework.samples.petclinic.dto.BaseForm;

/**
 * 
 * TODO add catpcha functionality, validation too
 * 
 * @author mplescano
 *
 */
//@NotRepeatedUser
public class UserForm extends BaseForm {

	@NotEmpty
	private String firstName;
	
	@NotEmpty
	private String lastName;
	
	@UniqueUsername
	@NotEmpty
	private String username;
	
	//@PasswordEquals()
	@NotEmpty
	private String password;
	
	@NotEmpty
	private String passwordAgain;
	
	@NotEmpty
	private String roles;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordAgain() {
		return passwordAgain;
	}

	public void setPasswordAgain(String passwordAgain) {
		this.passwordAgain = passwordAgain;
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}
	
	
	
}
