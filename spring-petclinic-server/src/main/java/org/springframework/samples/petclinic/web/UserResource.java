package org.springframework.samples.petclinic.web;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.dto.ResponseMessage;
import org.springframework.samples.petclinic.dto.form.UserForm;
import org.springframework.samples.petclinic.dto.form.UserQueryForm;
import org.springframework.samples.petclinic.dto.projection.UserForGridWeb;
import org.springframework.samples.petclinic.model.User;
import org.springframework.samples.petclinic.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author mplescano
 * @see http://stackoverflow.com/questions/19409492/how-to-achieve-pagination-table-layout-with-angular-js
 * @see http://www.baeldung.com/rest-api-pagination-in-spring
 */
@RestController
public class UserResource extends AbstractResourceController {

	private final UserService userService;
	
	@Autowired
	public UserResource(@Qualifier("userService") UserService userService) {
		this.userService = userService;
	}
	
	/**
	 * @see http://stackoverflow.com/questions/14216371/spring-mvc-form-validation-how-to-make-field-optional/14217761#14217761
	 * @param binder
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	}
	
    /**
     * Register User from public web
     */
    @PostMapping("/users/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseMessage registerUser(@Valid @RequestBody UserForm userForm) {
    	ResponseMessage message = null;
    	userService.save(userForm);
    	message = new ResponseMessage(true, "User registered");
    	
    	return message;
    }

    /**
     * Register User from public web
     */
    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasPermission()")
    public ResponseMessage createUser(@RequestBody UserForm userForm) {
        ResponseMessage message = null;
        userService.save(userForm);
        message = new ResponseMessage(true, "User created");
        return message;
    }
    
    @PutMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PreAuthorize("hasPermission()")
    public ResponseMessage modifyUser(@PathVariable Integer userId, @Valid @RequestBody UserForm userForm) {
        ResponseMessage message = null;
        userForm.setId(userId);
        userService.save(userForm);
        message = new ResponseMessage(true, "User modified");
        return message;
    }
    
    /**
     * @param pageable: it's always instantiated with default values.
     * @return
     */
    @GetMapping("/users")
    @PreAuthorize("hasPermission()")
    public Page<UserForGridWeb> findUserList(@SortDefault(sort = {"firstName"}, direction = Direction.ASC) Pageable pageable) {
        return userService.findUserForWebList(null, pageable);
    }
    
    @GetMapping("/users/{userId}")
    @PreAuthorize("hasPermission()")
    public UserForm findUser(@PathVariable Integer userId) {
        return userService.findUserForWeb(new UserQueryForm(userId));
    }
    
    @PostMapping("/users/filter")
    @PreAuthorize("hasPermission()")
    public Page<UserForGridWeb> findFilteredUserList(@Valid @RequestBody UserQueryForm userQueryForm, 
    		@SortDefault(sort = {"firstName"}, direction = Direction.ASC) Pageable pageable) {
        return userService.findUserForWebList(userQueryForm, pageable);
    }
    
    @DeleteMapping("/users")
    @PreAuthorize("hasPermission()")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseMessage deleteUserList(Integer[] userIds) {
    	
    	int deleted = userService.deleteUserList(userIds);
    	
    	return new ResponseMessage(true, "Successfuly deleted " + deleted + " items.");
    }
    
    @DeleteMapping("/users/{userId}")
    @PreAuthorize("hasPermission()")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseMessage deleteUser(@PathVariable Integer userId) {
        
        int deleted = userService.deleteUserList(new Integer[]{userId});
        
        return new ResponseMessage(true, "Successfuly deleted " + deleted + " items.");
    }
}
