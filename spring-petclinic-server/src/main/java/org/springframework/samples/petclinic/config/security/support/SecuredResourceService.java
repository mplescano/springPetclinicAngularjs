package org.springframework.samples.petclinic.config.security.support;

import java.util.List;

public interface SecuredResourceService {

	List<String> findAuthoritiesByResource(String resource);
	
}
