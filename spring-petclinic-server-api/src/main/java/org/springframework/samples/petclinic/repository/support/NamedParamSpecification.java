package org.springframework.samples.petclinic.repository.support;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.ParameterExpression;

import org.springframework.data.jpa.domain.Specification;

/**
 * @author mplescano
 *
 * @param <T>
 */
public abstract class NamedParamSpecification<T> implements Specification<T> {
	
	private Map<String, Object> parameters = new HashMap<>();
	
	protected <S> ParameterExpression<S> addParameter(CriteriaBuilder cb, Class<S> type, String paramName, S value) {
		parameters.put(paramName, value);
		return cb.parameter(type, paramName);
	}
	
	public Map<String, Object> getParameters() {
		return Collections.unmodifiableMap(parameters);
	}
}
