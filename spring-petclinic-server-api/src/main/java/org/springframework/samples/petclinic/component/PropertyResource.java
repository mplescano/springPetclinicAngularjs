package org.springframework.samples.petclinic.component;

import java.util.Properties;

import org.springframework.core.io.Resource;

public class PropertyResource {

	private final Resource resource;
	
	private Properties properties;
	
	private long currentTimestamp;
	
	public PropertyResource(Resource resource) {
		this.resource = resource;
	}

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
			
		} catch (Exception ex) {
			throw new PropertyResourceServiceException("Error in getProperties", ex);
		}
				
		return properties;
	}
	
	public String getValue(String key) {
		return getProperties().getProperty(key);
	}
	
	public static class PropertyResourceServiceException extends RuntimeException {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public PropertyResourceServiceException(String message, Throwable cause) {
			super(message, cause);
		}

		public PropertyResourceServiceException(Throwable cause) {
			super(cause);
		}
	}
}
