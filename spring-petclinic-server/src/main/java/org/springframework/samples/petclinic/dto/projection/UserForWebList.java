package org.springframework.samples.petclinic.dto.projection;

import java.util.Date;

public interface UserForWebList {

	Integer getId();
	
	String getFirstName();
	
	String getLastName();
	
	String getUsername();
	
	String getRoles();
	
	boolean isEnabled();
	
	Date getCreatedAt();
}
