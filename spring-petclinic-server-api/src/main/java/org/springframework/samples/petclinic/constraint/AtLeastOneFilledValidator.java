package org.springframework.samples.petclinic.constraint;

import java.beans.PropertyDescriptor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

/**
 * @author mplescano
 *
 */
public class AtLeastOneFilledValidator implements ConstraintValidator<AtLeastOneFilled, Object>{

	private AtLeastOneFilled constraintAnnotation;
	
	@Override
	public void initialize(AtLeastOneFilled constraintAnnotation) {
		this.constraintAnnotation = constraintAnnotation;
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		PropertyDescriptor[] arrPropDesc = BeanUtils.getPropertyDescriptors(value.getClass());
		if (constraintAnnotation.value().length > 0) {
			for (PropertyDescriptor propDesc : arrPropDesc) {
				for (String argProp : constraintAnnotation.value()) {
					if (argProp.equals(propDesc.getName())) {
						try {
							Object valueProp = propDesc.getReadMethod().invoke(value);
							if (valueProp != null && valueProp instanceof CharSequence && 
									StringUtils.hasText((CharSequence) valueProp)) {
								return true;
							}
							else if (valueProp != null) {
								return true;
							}
						} catch (Exception e) {
							// TODO: log warn
						}
					}
				}
			}
		}
		else {
			for (PropertyDescriptor propDesc : arrPropDesc) {
				if (!"class".equals(propDesc.getName())) {
					try {
						Object valueProp = propDesc.getReadMethod().invoke(value);
						if (valueProp != null && valueProp instanceof CharSequence && 
								StringUtils.hasText((CharSequence) valueProp)) {
							return true;
						}
						else if (valueProp != null) {
							return true;
						}
					} catch (Exception e) {
						//TODO log warn
					}
				}
			}
		}
		
		return false;
	}
}

