package org.springframework.samples.petclinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.samples.petclinic.model.Authority;

public interface AuthTokenRepository extends JpaRepository<Authority, Integer> {

}
