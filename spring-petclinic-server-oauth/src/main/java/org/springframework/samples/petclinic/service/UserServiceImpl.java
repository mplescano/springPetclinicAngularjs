package org.springframework.samples.petclinic.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.dto.UserDto;
import org.springframework.samples.petclinic.model.Authority;
import org.springframework.samples.petclinic.model.User;
import org.springframework.samples.petclinic.repository.AuthorityRepository;
import org.springframework.samples.petclinic.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Service("userService")
public class UserServiceImpl implements UserDetailsManager, UserService {
	
	private UserRepository userRepository;
	
	private AuthorityRepository authorityRepository;
	
    private PasswordEncoder passwordEncoder;
	
	private String rolePrefix = "";
	
	@Autowired
	public UserServiceImpl(UserRepository userRepository, AuthorityRepository authorityRepository, 
			PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.authorityRepository = authorityRepository;
		this.passwordEncoder = passwordEncoder;
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		List<User> users = userRepository.findByUsername(username);
		if (users.size() > 1 || users.size() == 0) {
			String message = "Query returned " + users.size() + " results for user '" + username + "'";
			
			throw new UsernameNotFoundException(message);
		}
		
		User user = users.get(0);
		
		List<GrantedAuthority> combinedAuthorities = new ArrayList<>();
		String[] roles = user.getRoles().split("[,]");
		for(String role : roles) {
			String prefixedRole = rolePrefix + role;
			Assert.isTrue(prefixedRole.startsWith("ROLE_"), prefixedRole
					+ " has to start with ROLE_");
			combinedAuthorities.add(new SimpleGrantedAuthority(prefixedRole));
			
			List<Authority> authorities = authorityRepository.findByRole(role);
			for (Authority authority : authorities) {
				combinedAuthorities.add(new SimpleGrantedAuthority(authority.getAuthority()));	
			}
			
		}
		
		return new UserDto(user.getId(), user.getUsername(), user.getPassword(),
				user.isEnabled(), true, true, true, combinedAuthorities);
	}

	@Transactional
	@Override
	public void createUser(UserDetails userDetails) {
		validateUserDetails(userDetails);
		User user = new User();
		user.setUsername(userDetails.getUsername());
		user.setPassword(userDetails.getPassword());
		user.setEnabled(userDetails.isEnabled());
		user.setCreatedAt(new Date());
		user.setFirstName(userDetails.getUsername());
		user.setLastName(userDetails.getUsername());
		
		List<String> roles = new ArrayList<>();
		List<GrantedAuthority> combinedAuthorities = (List<GrantedAuthority>) userDetails.getAuthorities();
		for (GrantedAuthority grantedAuthority : combinedAuthorities) {
			if (grantedAuthority.getAuthority().startsWith("ROLE_")) {
				roles.add(grantedAuthority.getAuthority());
			}
		}
		
		user.setRoles(StringUtils.arrayToDelimitedString(roles.toArray(new String[roles.size()]), ","));
		userRepository.save(user);
	}

	@Transactional
	@Override
	public void updateUser(UserDetails userDetails) {
		validateUserDetails(userDetails);

		List<User> users = userRepository.findByUsername(userDetails.getUsername());
		
	}

	@Transactional
	@Override
	public void deleteUser(String username) {
		userRepository.deleteByUsername(username);
	}

	@Override
	public void changePassword(String oldPassword, String newPassword) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean userExists(String username) {
		List<User> users = userRepository.findByUsername(username);
		if (users.size() >= 1) {
			return true;
		}
		
		return false;
	}

	private void validateUserDetails(UserDetails user) {
		Assert.hasText(user.getUsername(), "Username may not be empty or null");
		validateAuthorities(user.getAuthorities());
	}

	private void validateAuthorities(Collection<? extends GrantedAuthority> authorities) {
		Assert.notNull(authorities, "Authorities list must not be null");

		for (GrantedAuthority authority : authorities) {
			Assert.notNull(authority, "Authorities list contains a null entry");
			Assert.hasText(authority.getAuthority(),
					"getAuthority() method must return a non-empty string");
		}
	}
	
	public String getRolePrefix() {
		return rolePrefix;
	}

	public void setRolePrefix(String rolePrefix) {
		this.rolePrefix = rolePrefix;
	}

	@Override
	public int deleteUserList(Integer[] userIds) {
		return userRepository.deleteByIdInBatch(new HashSet<Integer>(Arrays.asList(userIds)));
	}

}
