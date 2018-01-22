package org.springframework.samples.petclinic.web;

import org.joda.time.LocalDateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.config.PropertiesConfig;
import org.springframework.samples.petclinic.config.security.MethodSecurityConfig;
import org.springframework.samples.petclinic.config.security.WebSecurityConfig;
import org.springframework.samples.petclinic.config.security.jwt.AuthTokenLogoutHandler;
import org.springframework.samples.petclinic.config.security.jwt.token.BuilderTokenStrategy;
import org.springframework.samples.petclinic.config.security.jwt.token.TokenStrategy;
import org.springframework.samples.petclinic.config.security.jwt.token.WrapperKey;
import org.springframework.samples.petclinic.dto.UserDto;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.service.AuthTokenService;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.samples.petclinic.support.JwtAuthRequestPostProcessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.JWTClaimsSet;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@WebMvcTest(PetResource.class)
@Import({WebSecurityConfig.class, MethodSecurityConfig.class, PropertiesConfig.class, AuthTokenLogoutHandler.class})
public class PetResourceTests {

    @Autowired
    private MockMvc mvc;
    
    @Autowired
    private WrapperKey wrapperKey;

    @MockBean
    ClinicService clinicService;
    
    @MockBean
    AuthTokenService authTokenService;
    
    @MockBean(name = "userService")
    UserDetailsManager userService;

    private UserDetails setupUser() {
    	List<GrantedAuthority> combinedAuthorities = new ArrayList<>();
    	combinedAuthorities.add(new SimpleGrantedAuthority("ROLE_VIEWER"));
    	combinedAuthorities.add(new SimpleGrantedAuthority("PERM_VIEW_PET"));
    	return new UserDto(999, "userMock", "passMock",
				true, true, true, true, combinedAuthorities);
    }
    
    @Test
    public void shouldGetAPetInJSonFormat() throws Exception {

        Pet pet = setupPet();

        given(clinicService.findPetById(2)).willReturn(pet);

        //given(userService.loadUserByUsername("userMock")).willReturn(setupUser());
        given(authTokenService.existsToken(Mockito.anyInt(), Mockito.anyString())).willReturn(true);

        mvc.perform(get("/rest/owner/2/pet/2").accept(MediaType.APPLICATION_JSON).with(new JwtAuthRequestPostProcessor(setupUser(), wrapperKey)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Basil"))
                .andExpect(jsonPath("$.type.id").value(6));
    }

    private Pet setupPet() {Owner owner = new Owner();
        owner.setFirstName("George");
        owner.setLastName("Bush");

        Pet pet = new Pet();

        pet.setName("Basil");
        pet.setId(2);

        PetType petType = new PetType();
        petType.setId(6);
        pet.setType(petType);

        owner.addPet(pet);
        return pet;
    }
}