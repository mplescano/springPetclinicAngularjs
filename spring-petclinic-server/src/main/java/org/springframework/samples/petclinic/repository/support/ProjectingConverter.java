package org.springframework.samples.petclinic.repository.support;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.projection.ProjectionFactory;

/**
 * @author mplescano
 *
 */
public class ProjectingConverter<T> implements Converter<Object, T> {

	private final ReturnedType type;
	
	private final ProjectionFactory factory;
	
	private final ConversionService conversionService = new DefaultConversionService();
	
	public ProjectingConverter(ReturnedType type, ProjectionFactory factory) {
		this.type = type;
		this.factory = factory;
	}
	
	@Override
	public T convert(Object source) {
		Class<T> targetType = (Class<T>) type.getReturnedType();

		if (targetType.isInterface()) {
			return factory.createProjection(targetType, getProjectionTarget(source));
		}

		return conversionService.convert(source, targetType);
	}

	private Object getProjectionTarget(Object source) {

		if (source != null && source.getClass().isArray()) {
			source = Arrays.asList((Object[]) source);
		}

		if (source instanceof Collection) {
			return toMap((Collection<?>) source, type.getInputProperties());
		}

		return source;
	}

	private static Map<String, Object> toMap(Collection<?> values, List<String> names) {

		int i = 0;
		Map<String, Object> result = new HashMap<String, Object>(values.size());

		for (Object element : values) {
			result.put(names.get(i++), element);
		}

		return result;
	}
}
