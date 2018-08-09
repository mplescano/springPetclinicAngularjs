package org.springframework.samples.petclinic.component.handler;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class OauthRestExceptionHandler extends RestExceptionHandler {

	public OauthRestExceptionHandler(MessageSourceAccessor messageSource) {
		super(messageSource);
	}

}
