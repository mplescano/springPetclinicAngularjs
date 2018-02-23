package org.springframework.samples.petclinic.dto;

public enum ErrorType {

	/**
	 * validation error, verification error
	 */
	DATA_ERROR("data.error"),
	
	/**
	 * unknown error or generic error
	 */
	INTERNAL_ERROR("internal.error"),
	
	/**
	 * external services, external files, internal files
	 */
	RESOURCE_ERROR("resource.error"),
	
	/**
	 * errors from the database, errors from repos, folders
	 */
	REPOSITORY_ERROR("repository.error"),
	
	/**
	 * errors in credentials
	 */
	AUTHENTICATION_ERROR("authentication.error"),
	
	/**
	 * Errors in token, errors in roles and permissions
	 */
	AUTHORIZATION_ERROR("authorization.error");

	private String code;

	ErrorType(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}