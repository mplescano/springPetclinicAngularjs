package org.springframework.samples.petclinic.constraint;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Payload;

@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface Compare {

	//public $compareAttribute;
	
	//public $compareValue;
	
	//public $allowEmpty=false;
	
	/**
	 * @var string the operator for comparison. Defaults to '='.
	 * The followings are valid operators:
	 * <ul>
	 * <li>'=' or '==': validates to see if the two values are equal. If {@link strict} is true, the comparison
	 * will be done in strict mode (i.e. checking value type as well).</li>
	 * <li>'!=': validates to see if the two values are NOT equal. If {@link strict} is true, the comparison
	 * will be done in strict mode (i.e. checking value type as well).</li>
	 * <li>'>': validates to see if the value being validated is greater than the value being compared with.</li>
	 * <li>'>=': validates to see if the value being validated is greater than or equal to the value being compared with.</li>
	 * <li>'<': validates to see if the value being validated is less than the value being compared with.</li>
	 * <li>'<=': validates to see if the value being validated is less than or equal to the value being compared with.</li>
	 * </ul>
	 */
	//public $operator='=';
	
	/**
	 * class number, date, string, or infer of the attributes
	 */
	//type
	
	/**
	 * @return the error message template
	 */
	String message() default "{org.springframework.samples.petclinic.constraint.Compare.message}";
	
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

		Compare[] value();
	}
	
}
