package org.springframework.samples.petclinic.constraint;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * @author mplescano
 *
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
@Constraint(validatedBy = AtLeastOneFilledValidator.class)
public @interface AtLeastOneFilled {

	/**
	 * Object's properties to validate
	 * @return
	 */
	String[] value() default { };
	
	/**
	 * @return the error message template
	 */
	String message() default "{org.springframework.samples.petclinic.constraint.AtLeastOneFilled.message}";
	
	Class<?>[] groups() default { };

	Class<? extends Payload>[] payload() default { };

	/**
	 * Defines several {@link UniqueUsername} annotations on the same element.
	 *
	 * @see UniqueUsername
	 */
	@Target({ TYPE })
	@Retention(RUNTIME)
	@Documented
	@interface List {

		AtLeastOneFilled[] value();
	}

}
