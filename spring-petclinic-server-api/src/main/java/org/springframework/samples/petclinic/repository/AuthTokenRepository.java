package org.springframework.samples.petclinic.repository;


import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.samples.petclinic.model.AuthToken;
import org.springframework.samples.petclinic.model.User;

public interface AuthTokenRepository extends JpaRepository<AuthToken, Integer> {

	void deleteByUserAndExpiryDateBefore(User user, Date dateTime);
	
	void deleteByUserAndExpiryDate(User user, Date dateTime);
	
	AuthToken findByUserAndToken(User user, String token);
}