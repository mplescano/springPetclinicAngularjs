package org.springframework.samples.petclinic.repository.support;

import static org.springframework.data.jpa.repository.query.QueryUtils.DELETE_ALL_QUERY_STRING;
import static org.springframework.data.jpa.repository.query.QueryUtils.getQueryString;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * @author mplescano
 *
 * @param <T>
 * @param <ID>
 */
public class ProjectableSpecificationExecutorImpl<T, ID extends Serializable> 
	extends SimpleJpaRepository<T, ID> implements ProjectableSpecificationExecutor<T, ID>, 
		BeanFactoryAware, BeanClassLoaderAware {

	private final EntityManager em;
	
	private final JpaEntityInformation<T, ?> entityInformation;
	
	private final SpelAwareProxyProjectionFactory factory;
	
	public ProjectableSpecificationExecutorImpl(JpaEntityInformation<T, ?> entityInformation,
			EntityManager entityManager) {
		super(entityInformation, entityManager);

	    // Keep the EntityManager around to be used from the newly introduced methods.
	    this.em = entityManager;
	    
	    this.entityInformation = entityInformation;
	    
	    factory = new SpelAwareProxyProjectionFactory();//TODO it has to be singleton
	}

	@Override
	public <S> Page<S> findProjectedAll(NamedParamSpecification<T> spec, Pageable pageable, Class<S> projectionClass) {
		ReturnedType type = ReturnedType.of(projectionClass, getDomainClass(), factory);
		Converter<Object, S> converter = new ProjectingConverter<>(type, factory);
		return findAll(spec, pageable).map(converter::convert);
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

		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		Long total = executeCountQuery(getCountQuery(spec, domainClass));
		List<S> content = total > pageable.getOffset() ? query.getResultList() : Collections.<S> emptyList();

		return new PageImpl<S>(content, pageable, total);
	}
	
	private static Long executeCountQuery(TypedQuery<Long> query) {

		Assert.notNull(query, "query must be not null");

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

		Assert.notNull(query, "query must be not null");
		Assert.notNull(domainClass, "domainClass must be not null");
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
	
	@Transactional
	public int deleteById(Iterable<ID> entities) {
		
		int deleted = 0;
		
		Assert.notNull(entities, "The given Iterable of entities not be null!");

		for (ID entity : entities) {
			deleteById(entity);
			deleted++;
		}
		return deleted;
	}
	
	@Transactional
	public int deleteByIdInBatch(Iterable<ID> entityIds) {

		Assert.notNull(entityIds, "The given Iterable of entities not be null!");

		if (!entityIds.iterator().hasNext()) {
			return 0;
		}

		int deletes = applyAndBind(getQueryString(DELETE_ALL_QUERY_STRING, entityInformation.getEntityName()), 
				entityInformation.getIdAttribute().getName(), entityIds, em)
				.executeUpdate();
		
		flush();
		
		return deletes;
	}
	
	public static <T> Query applyAndBind(String queryString, String attributeId, Iterable<T> entities, 
			EntityManager entityManager) {

		Assert.notNull(queryString, "queryString must be not null");
		Assert.notNull(entities, "entities must be not null");
		Assert.notNull(entityManager, "entityManager must be not null");

		Iterator<T> iterator = entities.iterator();

		if (!iterator.hasNext()) {
			return entityManager.createQuery(queryString);
		}

		String alias = QueryUtils.detectAlias(queryString);
		StringBuilder builder = new StringBuilder(queryString);
		builder.append(" where");

		int i = 0;

		while (iterator.hasNext()) {

			iterator.next();

			builder.append(String.format(" %s = ?%d", alias + "." + attributeId, ++i));

			if (iterator.hasNext()) {
				builder.append(" or");
			}
		}

		Query query = entityManager.createQuery(builder.toString());

		iterator = entities.iterator();
		i = 0;

		while (iterator.hasNext()) {
			query.setParameter(++i, iterator.next());
		}

		return query;
	}

    @Override
    public T findOne(NamedParamSpecification<T> spec) {
        try {
            TypedQuery<T> query = getQuery(spec, (Sort) null);
            if (spec != null) {
                    for (Entry<String, Object> entryParam : spec.getParameters().entrySet()) {
                            query.setParameter(entryParam.getKey(), entryParam.getValue());
                    }
            }
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public <S> S findProjectedOne(NamedParamSpecification<T> spec, Class<S> projectionClass) {
        ReturnedType type = ReturnedType.of(projectionClass, getDomainClass(), factory);
        return (new ProjectingConverter<S>(type, factory)).convert(findOne(spec));
    }

}
