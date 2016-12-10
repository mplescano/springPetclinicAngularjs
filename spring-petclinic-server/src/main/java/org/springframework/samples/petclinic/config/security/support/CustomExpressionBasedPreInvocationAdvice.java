package org.springframework.samples.petclinic.config.security.support;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.security.access.expression.ExpressionUtils;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.prepost.PreInvocationAttribute;
import org.springframework.security.access.prepost.PreInvocationAuthorizationAdvice;
import org.springframework.security.core.Authentication;

/**
 * Method pre-invocation handling based on expressions.
 *
 * @author Luke Taylor
 * @since 3.0
 */
public class CustomExpressionBasedPreInvocationAdvice implements
		PreInvocationAuthorizationAdvice {
	
	private MethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
	
	private SpelExpressionParser parser = new SpelExpressionParser();

	public boolean before(Authentication authentication, MethodInvocation mi,
			PreInvocationAttribute attr) {
		PreInvocationAttribute preAttr = (PreInvocationAttribute) attr;
		EvaluationContext ctx = expressionHandler.createEvaluationContext(authentication, mi);
		
		Expression preAuthorize = parser.parseExpression(preAttr.getAttribute());

		if (preAuthorize == null) {
			return true;
		}

		return ExpressionUtils.evaluateAsBoolean(preAuthorize, ctx);
	}

	public void setExpressionHandler(MethodSecurityExpressionHandler expressionHandler) {
		this.expressionHandler = expressionHandler;
	}
}