package org.springframework.samples.petclinic;

import javax.servlet.Filter;

import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.samples.petclinic.config.mvc.MvcViewConfig;
import org.springframework.samples.petclinic.config.mvc.MvcWebConfig;
import org.springframework.samples.petclinic.config.root.RootApplicationContextConfig;
import org.springframework.samples.petclinic.config.security.WebSecurityConfig;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class PetClinicServletInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class[]{WebSecurityConfig.class, RootApplicationContextConfig.class};
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class[]{MvcWebConfig.class, MvcViewConfig.class};
	}

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    @Override
    protected Filter[] getServletFilters() {
        // Used to provide the ability to enter Chinese characters inside the Owner Form
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter("UTF-8", true);
        return new Filter[]{characterEncodingFilter, new OpenEntityManagerInViewFilter()};
    }
}