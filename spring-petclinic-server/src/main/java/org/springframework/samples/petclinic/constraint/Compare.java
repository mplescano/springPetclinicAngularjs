package org.springframework.samples.petclinic.constraint;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Date;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Retention(RUNTIME)
@Target(TYPE)
@Constraint(validatedBy = CompareValidator.class)
public @interface Compare {
	
	String value();
	
	String compareAttribute() default "";
	
	String compareValue() default "";
	
	String formatValue() default "";
	
	boolean allowEmpty() default false;
	
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
	Operator operator() default Operator.EQUAL;
	
	/**
	 * class number, date, string, or infer of the attributes
	 */
	Type type() default Type.STRING;
	
	public static enum Type {
		
		NUMBER( Number.class ),
		
		DATE( Date.class ),
		
		STRING( CharSequence.class ),
		
		COMPARABLE( Comparable.class );
		
		private final Class<?> value;

		private Type(Class<?> value) {
			this.value = value;
		}

		public Class<?> getValue() {
			return value;
		}
	}
	
	/**
	 * @return the error message template
	 */
	String message() default "{org.springframework.samples.petclinic.constraint.Compare.message}";
	
	Class<?>[] groups() default { };

	Class<? extends Payload>[] payload() default { };
	
	public static enum Operator {

		EQUAL( 0 ),
		
		NOT_EQUAL( 1 ),
		
		GREATER_THAN( 2 ),
		
		GREATER_EQUAL_THAN( 3 ),
		
		LESS_THAN( 4 ),
		
		LESS_EQUAL_THAN( 5 );
		
		private final int value;

		private Operator(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}
	
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
