package org.springframework.samples.petclinic.repository.support;

import java.io.Serializable;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * @author mplescano
 *
 * @param <T>
 */
@NoRepositoryBean
public interface ProjectableSpecificationExecutor<T, ID extends Serializable> extends JpaSpecificationExecutor<T> {

	<S> Page<S> findProjectedAll(NamedParamSpecification<T> spec, Pageable pageable, Class<S> projectionClass);

	Page<T> findAll(NamedParamSpecification<T> spec, Pageable pageable);
	
	T findOne(NamedParamSpecification<T> spec);
	
	<S> S findProjectedOne(NamedParamSpecification<T> spec, Class<S> projectionClass);
	
	int deleteById(Iterable<ID> entityIds);
	
	int deleteByIdInBatch(Iterable<ID> entityIds);
}
