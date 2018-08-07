package org.springframework.samples.petclinic.component.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.samples.petclinic.component.PropertyResource;
import org.springframework.util.StringUtils;

public class DefaultResourceServiceImpl implements SecuredResourceService {

	private final PropertyResource propertyResource;
	
	public DefaultResourceServiceImpl(PropertyResource propertyResource) {
		this.propertyResource = propertyResource;
	}

	@Override
	public List<String> findAuthoritiesByResource(String resource) {
		List<String> results = new ArrayList<>();
		String arrAuhorities = propertyResource.getValue(resource);
		if (StringUtils.hasText(arrAuhorities)) {
			String[] authorities = arrAuhorities.split("[,]");
			for (String authority : authorities) {
				results.add(authority);
			}
		}
		return results;
	}

}
