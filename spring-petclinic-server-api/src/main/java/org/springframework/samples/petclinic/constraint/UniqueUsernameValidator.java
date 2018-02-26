package org.springframework.samples.petclinic.constraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.service.UserService;
import org.springframework.util.StringUtils;

/**
 * @author mplescano
 *
 */
public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String> {

    private UniqueUsername constraintAnnotation;

    @Autowired
    private UserService userService;

    @Override
    public void initialize(UniqueUsername constraintAnnotation) {
        this.constraintAnnotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
    	
        if (!StringUtils.hasText(value)) {
            return true;
        }

        if (userService.userExists(value)) {
            return false;
        }

        return true;
    }

}
