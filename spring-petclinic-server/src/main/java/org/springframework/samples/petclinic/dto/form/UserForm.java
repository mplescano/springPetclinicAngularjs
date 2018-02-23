package org.springframework.samples.petclinic.dto.form;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.samples.petclinic.constraint.Compare;
import org.springframework.samples.petclinic.constraint.UniqueUsername;
import org.springframework.samples.petclinic.constraint.scenariogroups.InsertScenario;
import org.springframework.samples.petclinic.constraint.scenariogroups.UpdateScenario;
import org.springframework.samples.petclinic.dto.BaseForm;
import org.springframework.samples.petclinic.model.User;

/**
 * 
 * TODO add catpcha functionality, validation too
 * 
 * @author mplescano
 *
 */
@Compare.List({
    @Compare(value = "password", compareAttribute = "passwordAgain", groups = {InsertScenario.class}),
    @Compare(value = "password", compareAttribute = "passwordAgain", groups = {UpdateScenario.class}, allowEmpty = true)
})
public class UserForm extends BaseForm {

    @NotEmpty(groups = {InsertScenario.class, UpdateScenario.class})
    private String firstName;

    @NotEmpty(groups = {InsertScenario.class, UpdateScenario.class})
    private String lastName;

    @UniqueUsername(groups = {InsertScenario.class})
    @NotEmpty(groups = {InsertScenario.class, UpdateScenario.class})
    private String username;

    @NotEmpty(groups = {InsertScenario.class})
    private String password;

    @NotEmpty(groups = {InsertScenario.class})
    private String passwordAgain;

    @NotEmpty(groups = {InsertScenario.class, UpdateScenario.class})
    private String roles;

    private Boolean enabled;

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
	
    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public static UserForm from(User user) {
        UserForm result = new UserForm();
        result.setId(user.getId());
        result.setFirstName(user.getFirstName());
        result.setLastName(user.getLastName());
        result.setRoles(user.getRoles());
        result.setEnabled(user.isEnabled());
        result.setPassword(user.getPassword());
        result.setUsername(user.getUsername());
        return result;
    }
}
