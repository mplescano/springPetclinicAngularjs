package org.springframework.samples.petclinic.web;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.dto.ResponseMessage;
import org.springframework.samples.petclinic.model.User;
import org.springframework.samples.petclinic.service.UserService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseMessage createUser(@Valid @RequestBody User user) {
    	ResponseMessage message = null;
    	if (userService.userExists(user.getUsername())) {
    		message = new ResponseMessage(false, "Username '" + user.getUsername() + "' is already taken");
    	}
    	
    	userService.save(user);
    	message = new ResponseMessage(true, null);
    	
    	return message;
    }
	
}
