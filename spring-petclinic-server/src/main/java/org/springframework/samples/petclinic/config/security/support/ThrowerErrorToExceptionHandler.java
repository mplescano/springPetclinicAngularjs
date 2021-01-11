package org.springframework.samples.petclinic.config.security.support;

import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ErrorProperties.IncludeStacktrace;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.error.ErrorAttributeOptions.Include;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.util.ThrowableAnalyzer;
import org.springframework.security.web.util.ThrowableCauseExtractor;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

@Controller
@RequestMapping("${server.error.path:${error.path:/error}}")
public class ThrowerErrorToExceptionHandler implements ErrorController {

	private final ErrorAttributes errorAttributes;

	private final ErrorProperties errorProperties;
	
	private ThrowableAnalyzer throwableAnalyzer = new DefaultThrowableAnalyzer();
	
	/**
	 * Create a new {@link BasicErrorController} instance.
	 * @param errorAttributes the error attributes
	 * @param errorProperties configuration properties
	 */
	@Autowired
	public ThrowerErrorToExceptionHandler(ErrorAttributes errorAttributes,
			ErrorProperties errorProperties) {
		Assert.notNull(errorAttributes, "ErrorAttributes must not be null");
		this.errorAttributes = errorAttributes;
		Assert.notNull(errorProperties, "ErrorProperties must not be null");
		this.errorProperties = errorProperties;
	}
	
	
	@Override
	public String getErrorPath() {
		return this.errorProperties.getPath();
	}
	
	@RequestMapping
	@ResponseBody
	public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) throws Exception {
		WebRequest webRequest = new ServletWebRequest(request);
		Throwable error = this.errorAttributes.getError(webRequest);
		if (error != null) {
			RuntimeException ase = null;
			// Try to extract a SpringSecurityException from the stacktrace
			Throwable[] causeChain = throwableAnalyzer.determineCauseChain(error);
			ase = (AuthenticationException) throwableAnalyzer.getFirstThrowableOfType(AuthenticationException.class, causeChain);
			if (ase != null) {
				//to RestControllerAdvice
				throw ase;
			}
			
			ase = (RuntimeException) throwableAnalyzer.getFirstThrowableOfType(RuntimeException.class, causeChain);
			if (ase != null) {
				//to RestControllerAdvice
				throw ase;
			}
			
			if (error instanceof Exception) {
				//to RestControllerAdvice
				throw (Exception) error;
			}
		}
		Map<String, Object> body = getErrorAttributes(request, isIncludeStackTrace(request, MediaType.ALL));
		HttpStatus status = getStatus(request);
		return new ResponseEntity<Map<String, Object>>(body, status);
	}

	protected HttpStatus getStatus(HttpServletRequest request) {
		Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
		if (statusCode == null) {
			return HttpStatus.INTERNAL_SERVER_ERROR;
		}
		try {
			return HttpStatus.valueOf(statusCode);
		}
		catch (Exception ex) {
			return HttpStatus.INTERNAL_SERVER_ERROR;
		}
	}
	
    /**
     * Returns a {@link Map} of the error attributes.
     * @param request the source request
     * @param includeStackTrace if stack trace elements should be included
     * @return the error attributes
     * @deprecated since 2.3.0 in favor of
     * {@link #getErrorAttributes(HttpServletRequest, ErrorAttributeOptions)}
     */
    @Deprecated
    protected Map<String, Object> getErrorAttributes(HttpServletRequest request, boolean includeStackTrace) {
        return getErrorAttributes(request,
                (includeStackTrace) ? ErrorAttributeOptions.of(Include.STACK_TRACE) : ErrorAttributeOptions.defaults());
    }

    protected Map<String, Object> getErrorAttributes(HttpServletRequest request, ErrorAttributeOptions options) {
        WebRequest webRequest = new ServletWebRequest(request);
        return this.errorAttributes.getErrorAttributes(webRequest, options);
    }
	
	/**
	 * Determine if the stacktrace attribute should be included.
	 * @param request the source request
	 * @param produces the media type produced (or {@code MediaType.ALL})
	 * @return if the stacktrace attribute should be included
	 */
	protected boolean isIncludeStackTrace(HttpServletRequest request,
			MediaType produces) {
		IncludeStacktrace include = errorProperties.getIncludeStacktrace();
		if (include == IncludeStacktrace.ALWAYS) {
			return true;
		}
		if (include == IncludeStacktrace.ON_TRACE_PARAM) {
			return getTraceParameter(request);
		}
		return false;
	}

	protected boolean getTraceParameter(HttpServletRequest request) {
		String parameter = request.getParameter("trace");
		if (parameter == null) {
			return false;
		}
		return !"false".equals(parameter.toLowerCase());
	}
	
	
	private static final class DefaultThrowableAnalyzer extends ThrowableAnalyzer {
		/**
		 * @see org.springframework.security.web.util.ThrowableAnalyzer#initExtractorMap()
		 */
		protected void initExtractorMap() {
			super.initExtractorMap();

			registerExtractor(ServletException.class, new ThrowableCauseExtractor() {
				public Throwable extractCause(Throwable throwable) {
					ThrowableAnalyzer.verifyThrowableHierarchy(throwable,
							ServletException.class);
					return ((ServletException) throwable).getRootCause();
				}
			});
		}

	}
}
