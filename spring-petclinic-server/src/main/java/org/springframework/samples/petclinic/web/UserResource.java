package org.springframework.samples.petclinic.web;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.dto.ResponseMessage;
import org.springframework.samples.petclinic.dto.form.UserForm;
import org.springframework.samples.petclinic.dto.form.UserQueryForm;
import org.springframework.samples.petclinic.dto.projection.UserForWebList;
import org.springframework.samples.petclinic.model.User;
import org.springframework.samples.petclinic.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
     * Create User
     */
    @RequestMapping(value = "/users", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseMessage createUser(@Valid @RequestBody UserForm userForm) {
    	ResponseMessage message = null;
    	userService.save(userForm);
    	message = new ResponseMessage(true, null);
    	
    	return message;
    }
	
    @GetMapping("/users")
    @PreAuthorize("hasPermission()")
    public Page<UserForWebList> findUserList(Pageable pageable) {
    	//TODO Do order by default
        return userService.findUserForWebList(null, pageable);
    }
    
    @PostMapping("/users/filter")
    @PreAuthorize("hasPermission()")
    public Page<UserForWebList> findFilteredUserList(@Valid @RequestBody UserQueryForm userQueryForm, 
    		Pageable pageable) {
        return userService.findUserForWebList(userQueryForm, pageable);
    }
}
