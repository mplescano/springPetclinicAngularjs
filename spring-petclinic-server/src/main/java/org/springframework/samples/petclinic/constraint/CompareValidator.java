package org.springframework.samples.petclinic.constraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author mplescano
 *
 */
public class CompareValidator implements ConstraintValidator<Compare, Object> {

	private Compare constraintAnnotation;
	
	@Override
	public void initialize(Compare constraintAnnotation) {
		this.constraintAnnotation = constraintAnnotation;
	}

	@Override
	public boolean isValid(Object object, ConstraintValidatorContext context) {
		//TODO use reflexion for accessing the properties...
		
		
		return false;
	}

}
