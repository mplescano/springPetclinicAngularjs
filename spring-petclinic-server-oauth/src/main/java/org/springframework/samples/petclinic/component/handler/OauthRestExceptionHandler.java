package org.springframework.samples.petclinic.component.handler;

import java.io.IOException;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.dto.ErrorType;
import org.springframework.samples.petclinic.dto.ResponseErrorMessage;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InsufficientScopeException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class OauthRestExceptionHandler extends DataRestExceptionHandler {

	public OauthRestExceptionHandler(MessageSourceAccessor messageSource) {
		super(messageSource);
	}

	@ExceptionHandler({ OAuth2Exception.class })
	public ResponseEntity<Object> handleOAuth2Exception(OAuth2Exception ex, WebRequest request) throws IOException {

		int status = ex.getHttpErrorCode();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Cache-Control", "no-store");
		headers.set("Pragma", "no-cache");
		ErrorType type = ErrorType.AUTHENTICATION_ERROR;
		if (status == HttpStatus.UNAUTHORIZED.value() || (ex instanceof InsufficientScopeException)) {
			type = ErrorType.AUTHORIZATION_ERROR;
			headers.set("WWW-Authenticate", String.format("%s %s", OAuth2AccessToken.BEARER_TYPE, ex.getSummary()));
		}

		ResponseErrorMessage message = new ResponseErrorMessage(generateCodeFromException(ex), type, ex.getMessage());

		return handleExceptionInternal(ex, message, null, HttpStatus.valueOf(ex.getHttpErrorCode()), request);
	}
}
