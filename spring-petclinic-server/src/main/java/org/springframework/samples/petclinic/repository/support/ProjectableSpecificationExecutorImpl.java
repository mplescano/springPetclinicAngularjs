package org.springframework.samples.petclinic.repository.support;

import java.io.Serializable;
import javax.persistence.EntityManager;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;

/**
 * @author mplescano
 *
 * @param <T>
 * @param <ID>
 */
public class ProjectableSpecificationExecutorImpl<T, ID extends Serializable> 
	extends SimpleJpaRepository<T, ID> implements ProjectableSpecificationExecutor<T>, 
		BeanFactoryAware, BeanClassLoaderAware {

	private final EntityManager entityManager;
	
	private final SpelAwareProxyProjectionFactory factory;
	
	public ProjectableSpecificationExecutorImpl(JpaEntityInformation<T, ?> entityInformation,
			EntityManager entityManager) {
		super(entityInformation, entityManager);

	    // Keep the EntityManager around to used from the newly introduced methods.
	    this.entityManager = entityManager;
	    
	    factory = new SpelAwareProxyProjectionFactory();
	}

	@Override
	public <S> Page<S> findProjectedAll(Specification<T> spec, Pageable pageable, Class<S> projectionClass) {
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
}
