package org.springframework.samples.petclinic.component.handler;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.dto.ErrorType;
import org.springframework.samples.petclinic.dto.ResponseErrorMessage;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

public class DataRestExceptionHandler extends RestExceptionHandler {

    public DataRestExceptionHandler(MessageSourceAccessor messageSource) {
		super(messageSource);
	}

	@ExceptionHandler({ DataAccessException.class })
    public ResponseEntity<Object> handleDataAccessException(Exception ex, WebRequest request) {
    	ResponseErrorMessage message = new ResponseErrorMessage(generateCodeFromException(ex), ErrorType.REPOSITORY_ERROR, ex.getMessage());
    	return handleExceptionInternal(ex, message, null, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
