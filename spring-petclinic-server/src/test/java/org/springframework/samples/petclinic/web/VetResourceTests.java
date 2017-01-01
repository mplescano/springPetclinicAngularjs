package org.springframework.samples.petclinic.web;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.config.security.MethodSecurityConfig;
import org.springframework.samples.petclinic.config.security.WebSecurityConfig;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

@RunWith(SpringRunner.class)
@WebMvcTest(VetResource.class)
@Import({WebSecurityConfig.class, MethodSecurityConfig.class})
public class VetResourceTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    ClinicService clinicService;

    @MockBean(name = "userService")
    UserDetailsManager userService;

    private UserDetails setupUser() {
    	List<GrantedAuthority> combinedAuthorities = new ArrayList<>();
    	combinedAuthorities.add(new SimpleGrantedAuthority("ROLE_VIEWER"));
    	combinedAuthorities.add(new SimpleGrantedAuthority("PERM_VIEW_VET"));
    	return new org.springframework.security.core.userdetails.User("userMock", "passMock",
				true, true, true, true, combinedAuthorities);
    }
    
    @Test
    public void shouldGetAListOfVetsInJSonFormat() throws Exception {

        Vet vet = new Vet();
        vet.setId(1);

        given(clinicService.findVets()).willReturn(Arrays.asList(vet));
        
        given(userService.loadUserByUsername("userMock")).willReturn(setupUser());

        mvc.perform(get("/rest/vets.json").accept(MediaType.APPLICATION_JSON).with(user(setupUser())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }


}
