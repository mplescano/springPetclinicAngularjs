package org.springframework.samples.petclinic.constraint;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Locale;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.format.datetime.DateFormatter;
import org.springframework.format.number.NumberStyleFormatter;
import org.springframework.samples.petclinic.constraint.Compare.Operator;
import org.springframework.samples.petclinic.constraint.Compare.Type;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * @author mplescano
 *
 */
public class CompareValidator implements ConstraintValidator<Compare, Object> {

	private Compare constraintAnnotation;

	private Operator operator;
	
	private PaserValue<?> parserValue;
	
	@Override
	public void initialize(Compare constraintAnnotation) {
		this.constraintAnnotation = constraintAnnotation;
		operator = this.constraintAnnotation.operator();

		switch (this.constraintAnnotation.type()) {
		case NUMBER:
			parserValue = new PaserValue<Number>(constraintAnnotation);
			break;
			
		case DATE:
			parserValue = new PaserValue<Date>(constraintAnnotation);
			break;
			
		case COMPARABLE:
			parserValue = new PaserValue<Comparable<?>>(constraintAnnotation);
			break;

		case STRING:
		default:
			parserValue = new PaserValue<CharSequence>(constraintAnnotation);
			break;
		}
		
	}

	@Override
	public boolean isValid(Object parentObject, ConstraintValidatorContext context) {
		if (parentObject == null) {
			return true;
		}

		try {
			Object value = parserValue.getValue(parentObject);
			Object compareValue = parserValue.getCompareValue(parentObject);
			if (value == null && parserValue.isAllowEmpty()) {
				return true;
			}
			if (value != null && value instanceof CharSequence && !StringUtils.hasText((CharSequence) value) 
					&& parserValue.isAllowEmpty()) {
				return true;
			}
			if (value == null) {
				return false;
			}
			switch (operator) {
			case EQUAL:
				if (value.equals(compareValue)) {
					return true;
				}
				break;
			
			case GREATER_THAN:
				if (value instanceof Comparable<?> && compareValue instanceof Comparable<?>) {
					return ((Comparable) value).compareTo(compareValue) == 1;
				}
				break;
				
			case GREATER_EQUAL_THAN:
				if (value instanceof Comparable<?> && compareValue instanceof Comparable<?>) {
					return ((Comparable) value).compareTo(compareValue) == 0 || 
							((Comparable) value).compareTo(compareValue) == 1;
				}
				break;
				
			case LESS_THAN:
				if (value instanceof Comparable<?> && compareValue instanceof Comparable<?>) {
					return ((Comparable) value).compareTo(compareValue) == -1;
				}
				break;
				
			case LESS_EQUAL_THAN:
				if (value instanceof Comparable<?> && compareValue instanceof Comparable<?>) {
					return ((Comparable) value).compareTo(compareValue) == -1 ||
							((Comparable) value).compareTo(compareValue) == 0;
				}
				break;
				
			default:
				break;
			}
		} catch (Exception e) {
			return false;
		}

		return false;
	}
	
	public static class PaserValue<T> {
		
		private Type type;

		private String attribute;
		
		private String compareAttribute;

		private String value;

		private String formatValue;
		
		private boolean allowEmpty;
		
		public PaserValue(Compare constraintAnnotation) {
			type = constraintAnnotation.type();
			compareAttribute = constraintAnnotation.compareAttribute();
			value = constraintAnnotation.compareValue();
			formatValue = constraintAnnotation.formatValue();
			attribute = constraintAnnotation.value();
			allowEmpty = constraintAnnotation.allowEmpty();

			if ((!StringUtils.hasText(compareAttribute) && !StringUtils.hasText(value))) {
				throw new IllegalArgumentException("compareAttribute or compareValue has to be filled");
			}
			
			Assert.hasText(attribute, "attribute is required");
		}
		
		public T getValue(Object object) throws Exception {
			T returnValue = null;
			Method getterAttribute = object.getClass().getMethod("get" + StringUtils.capitalize(attribute));
			Object getterValue = getterAttribute.invoke(object);
			returnValue = (T) getterValue;
			return returnValue;
		}
		
		public T getCompareValue(Object object) throws Exception {
			T returnCompareValue = null;
			if (compareAttribute != null) {
				Method getterCompareAttribute = object.getClass().getMethod("get" + StringUtils.capitalize(compareAttribute));
				Object getterCompareValue = getterCompareAttribute.invoke(object);
				returnCompareValue = (T) getterCompareValue;
			}
			else if (value != null) {
				switch (type) {
				case NUMBER:
					NumberStyleFormatter numberFormatter = null;
					if (formatValue != null) {
						numberFormatter = new NumberStyleFormatter(formatValue);
					}
					else {
						numberFormatter = new NumberStyleFormatter();
					}
					returnCompareValue = (T) numberFormatter.parse(value, Locale.ENGLISH);
					break;
					
				case DATE:
					DateFormatter dateFormatter = null;
					if (formatValue != null) {
						dateFormatter = new DateFormatter(formatValue);
					}
					else {
						dateFormatter = new DateFormatter();
					}
					returnCompareValue = (T) dateFormatter.parse(value, Locale.ENGLISH);
					break;
					
				case COMPARABLE:
					Class<?> clazz = ClassUtils.forName(value, ClassUtils.getDefaultClassLoader());
					returnCompareValue = (T) ClassUtils.getConstructorIfAvailable(clazz).newInstance();
					break;

				case STRING:
				default:
					returnCompareValue = (T) value;
					break;
				}
			}
			
			return returnCompareValue;
		}

		public Type getType() {
			return type;
		}

		public boolean isAllowEmpty() {
			return allowEmpty;
		}
		
	}

}
