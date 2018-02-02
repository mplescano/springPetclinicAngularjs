package org.springframework.samples.petclinic.config.security.jwt;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.dto.ResponseMessage;
import org.springframework.samples.petclinic.dto.UserDto;
import org.springframework.samples.petclinic.service.AuthTokenService;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AuthTokenLogoutHandler implements LogoutHandler {

    private AuthTokenService authTokenService;

    private final ObjectMapper mapper;

    public AuthTokenLogoutHandler(AuthTokenService authTokenService, ObjectMapper mapper) {
        this.authTokenService = authTokenService;
        this.mapper = mapper;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Integer userId = null;
        if (authentication == null) {
            throw new InsufficientAuthenticationException("Token principal cannot be null!");
        }
        try {
            if (authentication.getPrincipal() instanceof UserDto) {
                UserDto principalWebHolder = (UserDto) authentication.getPrincipal();
                userId = principalWebHolder.getId();
            }

            authTokenService.removeTokenByUserId(userId);
            ResponseMessage message = new ResponseMessage(true, "Successful logout");
            response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            mapper.writeValue(response.getWriter(), message);
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException("Error responding the logout", ex);
        }
    }
}