package org.springframework.samples.petclinic.config.security.support;

import java.util.Collection;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.samples.petclinic.config.mvc.support.WebPreInvocationAttribute;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.prepost.PreInvocationAttribute;
import org.springframework.security.access.prepost.PreInvocationAuthorizationAdvice;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;

/**
 * Voter which performs the actions using a PreInvocationAuthorizationAdvice
 * implementation generated from @PreFilter and @PreAuthorize annotations.
 * <p>
 * In practice, if these annotations are being used, they will normally contain all the
 * necessary access control logic, so a voter-based system is not really necessary and a
 * single <tt>AccessDecisionManager</tt> which contained the same logic would suffice.
 * However, this class fits in readily with the traditional voter-based
 * <tt>AccessDecisionManager</tt> implementations used by Spring Security.
 *
 * @author Luke Taylor
 * @since 3.0
 */
public class CustomPreInvocationAuthorizationAdviceVoter implements
		AccessDecisionVoter<FilterInvocation> {

	private final PreInvocationAuthorizationAdvice preAdvice;

	public CustomPreInvocationAuthorizationAdviceVoter(PreInvocationAuthorizationAdvice pre) {
		this.preAdvice = pre;
	}

	public boolean supports(ConfigAttribute attribute) {
		return attribute instanceof PreInvocationAttribute;
	}

	public boolean supports(Class<?> clazz) {
		return FilterInvocation.class.isAssignableFrom(clazz);
	}

	public int vote(Authentication authentication, FilterInvocation fi,
			Collection<ConfigAttribute> attributes) {

		// Find prefilter and preauth (or combined) attributes
		// if both null, abstain
		// else call advice with them

		PreInvocationAttribute preAttr = findPreInvocationAttribute(attributes);

		if (preAttr == null) {
			// No expression based metadata, so abstain
			return ACCESS_ABSTAIN;
		}
		MethodInvocation method = ((WebPreInvocationAttribute) preAttr).getMethodInvocation();
		boolean allowed = preAdvice.before(authentication, method, preAttr);

		return allowed ? ACCESS_GRANTED : ACCESS_DENIED;
	}

	private PreInvocationAttribute findPreInvocationAttribute(
			Collection<ConfigAttribute> config) {
		for (ConfigAttribute attribute : config) {
			if (attribute instanceof PreInvocationAttribute) {
				return (PreInvocationAttribute) attribute;
			}
		}

		return null;
	}
}