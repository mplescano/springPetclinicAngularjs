package org.springframework.samples.petclinic.component.security;

import java.util.List;

public interface SecuredResourceService {

	List<String> findAuthoritiesByResource(String resource);
	
}
