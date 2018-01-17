package org.springframework.samples.petclinic.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "users")
public class User extends Person {

	@Column(name = "roles")
	@NotEmpty
	private String roles;

	@Column(name = "username")
	@NotEmpty
	private String username;

	@Column(name = "password")
	@NotEmpty
	private String password;
	
	@Column(name = "enabled")
	private boolean enabled;
	
	@Column(name = "created_at")
	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	private Date createdAt;
	
	public User() {
	}
	
	public User(Integer id) {
		this.id = id;
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
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

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
}