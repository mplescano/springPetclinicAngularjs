package org.springframework.samples.petclinic.dto;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class UserDto extends User {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer id;

	public UserDto(Integer id, String username, String password, boolean enabled, boolean accountNonExpired,
			boolean credentialsNonExpired, boolean accountNonLocked,
			Collection<? extends GrantedAuthority> authorities) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
		this.id = id;
	}

	public Integer getId() {
		return id;
	}
}
