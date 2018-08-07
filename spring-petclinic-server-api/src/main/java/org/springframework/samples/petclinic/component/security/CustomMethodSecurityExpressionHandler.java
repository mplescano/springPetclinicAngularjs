package org.springframework.samples.petclinic.component.security;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.expression.OAuth2ExpressionParser;
import org.springframework.security.oauth2.provider.expression.OAuth2SecurityExpressionMethods;
import org.springframework.util.Assert;

public class CustomMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {
	
    private AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();
    
    private final SecuredResourceService aclSecuredResourceService;
    
    private final SecuredResourceService scopeSecuredResourceService;
    
	public CustomMethodSecurityExpressionHandler(
			SecuredResourceService aclSecuredResourceService,
			SecuredResourceService scopeSecuredResourceService) {
    	Assert.notNull(aclSecuredResourceService, "A acl securedResourceService is required");
    	Assert.notNull(scopeSecuredResourceService, "A scope securedResourceService is required");
    	this.aclSecuredResourceService = aclSecuredResourceService;
    	this.scopeSecuredResourceService = scopeSecuredResourceService;
		setExpressionParser(new OAuth2ExpressionParser(getExpressionParser()));
    }

    @Override
    public void setReturnObject(Object returnObject, EvaluationContext ctx) {
        ((MethodSecurityExpressionRoot) ctx.getRootObject().getValue()).setReturnObject(returnObject);
    }

	/**
	 * Sets the {@link AuthenticationTrustResolver} to be used. The default is
	 * {@link AuthenticationTrustResolverImpl}.
	 *
	 * @param trustResolver the {@link AuthenticationTrustResolver} to use. Cannot be
	 * null.
	 */
    @Override
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
        root.setAclSecuredResourceService(aclSecuredResourceService);
        root.setScopeSecuredResourceService(scopeSecuredResourceService);

        return root;
    }

	@Override
	public StandardEvaluationContext createEvaluationContextInternal(Authentication authentication, MethodInvocation mi) {
		StandardEvaluationContext ec = super.createEvaluationContextInternal(authentication, mi);
		ec.setVariable("oauth2", new OAuth2SecurityExpressionMethods(authentication));
		return ec;
	}
}
