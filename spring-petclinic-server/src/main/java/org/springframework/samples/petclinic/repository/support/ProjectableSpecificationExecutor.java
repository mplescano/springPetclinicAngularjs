package org.springframework.samples.petclinic.repository.support;

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
public interface ProjectableSpecificationExecutor<T> extends JpaSpecificationExecutor<T> {

	<S> Page<S> findProjectedAll(NamedParamSpecification<T> spec, Pageable pageable, Class<S> projectionClass);

	Page<T> findAll(NamedParamSpecification<T> spec, Pageable pageable);
}
