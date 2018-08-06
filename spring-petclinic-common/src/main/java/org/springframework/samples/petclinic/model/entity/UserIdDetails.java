package org.springframework.samples.petclinic.model.entity;

import org.springframework.security.core.userdetails.UserDetails;

public interface UserIdDetails extends UserDetails {

	Long getId();
}