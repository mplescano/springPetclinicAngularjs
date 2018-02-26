package org.springframework.samples.petclinic.config.security.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

public class PropertySecuredResourceServiceImpl implements SecuredResourceService {

	private Resource resource;
	
	private Properties properties;
	
	private long currentTimestamp;

	private Properties getProperties() {
		try {
			if (properties == null) {
				currentTimestamp = resource.lastModified();
				
				properties = new Properties();
				properties.load(resource.getInputStream());
			}
			
			if (resource.lastModified() > currentTimestamp) {
				properties = new Properties();
				properties.load(resource.getInputStream());
			}
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
				
		return properties;
	}
	
	@Override
	public List<String> findAuthoritiesByResource(String resource) {
		List<String> results = new ArrayList<>();
		Properties securedResources = getProperties();
		String arrAuhorities = securedResources.getProperty(resource);
		if (StringUtils.hasText(arrAuhorities)) {
			String[] authorities = arrAuhorities.split("[,]");
			for (String authority : authorities) {
				results.add(authority);
			}
		}
		return results;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

}
