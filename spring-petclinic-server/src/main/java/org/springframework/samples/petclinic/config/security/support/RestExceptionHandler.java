package org.springframework.samples.petclinic.config.security.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.NestedRuntimeException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.config.security.jwt.token.TokenExpiredException;
import org.springframework.samples.petclinic.config.security.jwt.token.TokenInvalidedException;
import org.springframework.samples.petclinic.dto.DetailErrorMessage;
import org.springframework.samples.petclinic.dto.ResponseMessage;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * 
 * @author mplescano
 * @see http://www.baeldung.com/exception-handling-for-rest-with-spring
 * @see http://springinpractice.com/2013/10/09/generating-json-error-object-responses-with-spring-web-mvc
 * @see https://www.petrikainulainen.net/programming/spring-framework/spring-from-the-trenches-adding-validation-to-a-rest-api/
 * 
 */
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);

    private static final String VALIDATION_ERRORS_MESSAGE = "validation.errors"; 
    
    private MessageSourceAccessor messageSource;
    
    @Autowired
    public RestExceptionHandler(MessageSourceAccessor messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * Finally, let's see how to handle method level security @PreAuthorize, @PostAuthorize and @Secure Access Denied.
     * We'll of course use the global exception handling mechanism that we discussed earlier to handle 
     * the new AccessDeniedException as well:
     * 
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler({ TokenInvalidedException.class, InsufficientAuthenticationException.class, AccessDeniedException.class })
    public ResponseEntity<Object> handleAccessDeniedException(Exception ex, WebRequest request) {
    	ResponseMessage message = new ResponseMessage(false, ex.getMessage());
        return handleExceptionInternal(ex, message, null, HttpStatus.FORBIDDEN, request);
    }
    
    @ExceptionHandler({ PersistenceException.class, DataAccessException.class, NullPointerException.class, NestedRuntimeException.class })
    public ResponseEntity<Object> handlePersistenceException(Exception ex, WebRequest request) {
    	ResponseMessage message = new ResponseMessage(false, ex.getMessage());
    	return handleExceptionInternal(ex, message, null, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
    
    @ExceptionHandler({ AccountStatusException.class, BadCredentialsException.class, UsernameNotFoundException.class, 
    	InternalAuthenticationServiceException.class })
    public ResponseEntity<Object> handleAuthenticateException(Exception ex, WebRequest request) {
    	ResponseMessage message = new ResponseMessage(false, ex.getMessage());
    	return handleExceptionInternal(ex, message, null, HttpStatus.UNAUTHORIZED, request);
    }
	
    @ExceptionHandler({ TokenExpiredException.class })
    @ResponseBody
    public ResponseMessage tokenExpiredException(Exception ex, WebRequest webRequest) {
        if (webRequest instanceof ServletWebRequest) {
            ServletWebRequest servletRequest = (ServletWebRequest) webRequest;
            HttpServletResponse response = servletRequest.getNativeResponse(HttpServletResponse.class);
            if (!response.isCommitted()) {
                response.setStatus(440);//440 Login Time-out The client's session has expired and must log in again.[76]
            }
        }
        ResponseMessage message = new ResponseMessage(false, ex.getMessage());
    	return message;
    }
    
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        BindingResult result = ex.getBindingResult();
        List<ObjectError> objectErrors = result.getAllErrors();

        return handleExceptionInternal(ex, processObjectErrors(objectErrors, ex), headers, status, request);
    }
    
    private ResponseMessage processObjectErrors(List<ObjectError> objectErrors, Exception ex) {
        ResponseMessage message = null;
        List<DetailErrorMessage> detailFields = new ArrayList<>();

        for (final ObjectError objError: objectErrors) {
            if (objError instanceof FieldError) {
                detailFields.add(new DetailErrorMessage(((FieldError) objError).getField(), resolveLocalizedErrorMessage(objError)));
            }
            else {
                detailFields.add(new DetailErrorMessage(objError.getCode(), resolveLocalizedErrorMessage(objError)));
            }
        }

        message = new ResponseMessage(false, messageSource.getMessage(VALIDATION_ERRORS_MESSAGE), detailFields);

        return message;
    }
    
    private String resolveLocalizedErrorMessage(ObjectError objError) {
        String localizedErrorMessage = messageSource.getMessage(objError);
 
        //If the message was not found, return the most accurate field error code instead.
        //You can remove this check if you prefer to get the default error message.
        if (localizedErrorMessage.equals(objError.getDefaultMessage()) && 
                localizedErrorMessage.startsWith("{") && localizedErrorMessage.endsWith("}")) {
            String[] fieldErrorCodes = objError.getCodes();
            if (logger.isDebugEnabled()) {
                for (String errorCode: fieldErrorCodes) {
                    logger.debug("errorCode available: " + errorCode);
                }
            }
            localizedErrorMessage = fieldErrorCodes[0];
        }
 
        return localizedErrorMessage;
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex,
                                                             Object body,
                                                             HttpHeaders headers,
                                                             HttpStatus status,
                                                             WebRequest request) {
        logger.error("Error", ex);
        return super.handleExceptionInternal(ex, body, headers, status, request);
    }
}