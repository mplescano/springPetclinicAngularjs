package org.springframework.samples.petclinic.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.samples.petclinic.model.User;
import org.springframework.samples.petclinic.repository.support.ProjectableSpecificationExecutor;

/**
 * @author mplescano
 *
 */
public interface UserRepository extends JpaRepository<User, Integer>, ProjectableSpecificationExecutor<User, Integer> {

	List<User> findByUsername(String username);

	Integer deleteByUsername(String username);
	
}
