package org.springframework.samples.petclinic.repository.support;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.util.Assert;

/**
 * @author mplescano
 *
 * @param <T>
 * @param <ID>
 */
public class ProjectableSpecificationExecutorImpl<T, ID extends Serializable> 
	extends SimpleJpaRepository<T, ID> implements ProjectableSpecificationExecutor<T>, 
		BeanFactoryAware, BeanClassLoaderAware {

	private final EntityManager em;
	
	private final SpelAwareProxyProjectionFactory factory;
	
	public ProjectableSpecificationExecutorImpl(JpaEntityInformation<T, ?> entityInformation,
			EntityManager entityManager) {
		super(entityInformation, entityManager);

	    // Keep the EntityManager around to used from the newly introduced methods.
	    this.em = entityManager;
	    
	    factory = new SpelAwareProxyProjectionFactory();//TODO it has to be singleton
	}

	@Override
	public <S> Page<S> findProjectedAll(NamedParamSpecification<T> spec, Pageable pageable, Class<S> projectionClass) {
		ReturnedType type = ReturnedType.of(projectionClass, getDomainClass(), factory);
		return findAll(spec, pageable).map(new ProjectingConverter<S>(type, factory));
	}

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		factory.setBeanClassLoader(classLoader);
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		factory.setBeanFactory(beanFactory);
	}

	@Override
	public Page<T> findAll(NamedParamSpecification<T> spec, Pageable pageable) {
		TypedQuery<T> query = getQuery(spec, pageable);
		if (spec != null) {
			for (Entry<String, Object> entryParam : spec.getParameters().entrySet()) {
				query.setParameter(entryParam.getKey(), entryParam.getValue());
			}
		}
		return pageable == null ? new PageImpl<T>(query.getResultList()) : readPage(query, pageable, spec);
	}
	
	protected Page<T> readPage(TypedQuery<T> query, Pageable pageable, NamedParamSpecification<T> spec) {
		return readPage(query, getDomainClass(), pageable, spec);
	}
	
	protected <S extends T> Page<S> readPage(TypedQuery<S> query, Class<S> domainClass, Pageable pageable,
			NamedParamSpecification<S> spec) {

		query.setFirstResult(pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		Long total = executeCountQuery(getCountQuery(spec, domainClass));
		List<S> content = total > pageable.getOffset() ? query.getResultList() : Collections.<S> emptyList();

		return new PageImpl<S>(content, pageable, total);
	}
	
	private static Long executeCountQuery(TypedQuery<Long> query) {

		Assert.notNull(query);

		List<Long> totals = query.getResultList();
		Long total = 0L;

		for (Long element : totals) {
			total += element == null ? 0 : element;
		}

		return total;
	}
	
	protected <S extends T> TypedQuery<Long> getCountQuery(NamedParamSpecification<S> spec, Class<S> domainClass) {

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Long> query = builder.createQuery(Long.class);

		Root<S> root = applySpecificationToCriteria(spec, domainClass, query);

		if (query.isDistinct()) {
			query.select(builder.countDistinct(root));
		} else {
			query.select(builder.count(root));
		}
		
		// Remove all Orders the Specifications might have applied
		query.orderBy(Collections.<Order> emptyList());

		TypedQuery<Long> typedQuery = em.createQuery(query);
		
		if (spec != null) {
			for (Entry<String, Object> entryParam : spec.getParameters().entrySet()) {
				typedQuery.setParameter(entryParam.getKey(), entryParam.getValue());
			}
		}
		
		return typedQuery;
	}
	
	private <S, U extends T> Root<U> applySpecificationToCriteria(NamedParamSpecification<U> spec, Class<U> domainClass,
			CriteriaQuery<S> query) {

		Assert.notNull(query);
		Assert.notNull(domainClass);
		Root<U> root = query.from(domainClass);

		if (spec == null) {
			return root;
		}

		CriteriaBuilder builder = em.getCriteriaBuilder();
		Predicate predicate = spec.toPredicate(root, query, builder);

		if (predicate != null) {
			query.where(predicate);
		}

		return root;
	}
}
