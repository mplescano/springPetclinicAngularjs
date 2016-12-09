package org.springframework.samples.petclinic.config.security.support;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.expression.EvaluationContext;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.util.Assert;

public class CustomMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {
	
    private AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();
    
    private SecuredResourceService securedResourceService;
    
    public CustomMethodSecurityExpressionHandler(SecuredResourceService securedResourceService) {
    	this.securedResourceService = securedResourceService;
    }

    @Override
    public void setReturnObject(Object returnObject, EvaluationContext ctx) {
        ((MethodSecurityExpressionRoot) ctx.getRootObject().getValue()).setReturnObject(returnObject);
    }

	/*public void setReturnObject(Object returnObject, EvaluationContext ctx) {
		((MethodSecurityExpressionOperations) ctx.getRootObject().getValue())
				.setReturnObject(returnObject);
	}*/

	/**
	 * Sets the {@link AuthenticationTrustResolver} to be used. The default is
	 * {@link AuthenticationTrustResolverImpl}.
	 *
	 * @param trustResolver the {@link AuthenticationTrustResolver} to use. Cannot be
	 * null.
	 */
	public void setTrustResolver(AuthenticationTrustResolver trustResolver) {
		Assert.notNull(trustResolver, "trustResolver cannot be null");
		super.setTrustResolver(trustResolver);
		this.trustResolver = trustResolver;
	}
    
    @Override
    protected MethodSecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication,
        MethodInvocation invocation) {
        final MethodSecurityExpressionRoot root = new MethodSecurityExpressionRoot(authentication);
        root.setThis(invocation.getThis());
        root.setMethod(invocation.getMethod());
        root.setPermissionEvaluator(getPermissionEvaluator());
        root.setTrustResolver(this.trustResolver);
        root.setRoleHierarchy(getRoleHierarchy());
        root.setSecuredResourceService(securedResourceService);

        return root;
    }

}
