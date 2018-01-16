package org.springframework.samples.petclinic.config.security.jwt.token;

import org.springframework.beans.factory.FactoryBean;

public class BuilderTokenStrategyFactory implements FactoryBean<BuilderTokenStrategy>{

	@Override
	public BuilderTokenStrategy getObject() throws Exception {
		return new BuilderTokenStrategy();
	}

	@Override
	public Class<?> getObjectType() {
		return BuilderTokenStrategy.class;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}
}