package org.springframework.samples.petclinic.component.security;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.expression.OAuth2ExpressionUtils;


public class MethodSecurityExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {

	private SecuredResourceService aclSecuredResourceService;
	
	private SecuredResourceService scopeSecuredResourceService;
	
	private Object filterObject;
	
	private Object returnObject;
	
	private Object target;

	private Method method;
	
	public MethodSecurityExpressionRoot(Authentication authentication) {
		super(authentication);
	}

	public void setFilterObject(Object filterObject) {
		this.filterObject = filterObject;
	}

	public Object getFilterObject() {
		return filterObject;
	}

	public void setReturnObject(Object returnObject) {
		this.returnObject = returnObject;
	}

	public Object getReturnObject() {
		return returnObject;
	}
	
	public boolean hasPermission() {
		Set<String> userAuthorities = getAuthoritySet();
		List<String> resourcePermissions = aclSecuredResourceService.findAuthoritiesByResource(method.getDeclaringClass().getName() + "." + method.getName());
		
		for (String userAuthority : userAuthorities) {
			if (resourcePermissions.contains(userAuthority)) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean hasScope() {
		List<String> resourceScope = scopeSecuredResourceService.findAuthoritiesByResource(method.getDeclaringClass().getName() + "." + method.getName());
		
		boolean result = OAuth2ExpressionUtils.hasAnyScope(authentication, resourceScope.toArray(new String[resourceScope.size()]));
		
		return result;
	}

	private Set<String> getAuthoritySet() {
		Set<String> roles = new HashSet<String>();
		Collection<? extends GrantedAuthority> userAuthorities = authentication.getAuthorities();

		/*if (roleHierarchy != null) {
			userAuthorities = roleHierarchy
					.getReachableGrantedAuthorities(userAuthorities);
		}*/

		roles = AuthorityUtils.authorityListToSet(userAuthorities);

		return roles;
	}
	
	/**
	 * Sets the "this" property for use in expressions. Typically this will be the "this"
	 * property of the {@code JoinPoint} representing the method invocation which is being
	 * protected.
	 *
	 * @param target the target object on which the method in is being invoked.
	 */
	void setThis(Object target) {
		this.target = target;
	}

	public Object getThis() {
		return target;
	}
	
	void setMethod(Method method) {
		this.method = method;
	}

	public void setAclSecuredResourceService(SecuredResourceService securedResourceService) {
		this.aclSecuredResourceService = securedResourceService;
	}

	public void setScopeSecuredResourceService(
			SecuredResourceService scopeSecuredResourceService) {
		this.scopeSecuredResourceService = scopeSecuredResourceService;
	}
}
