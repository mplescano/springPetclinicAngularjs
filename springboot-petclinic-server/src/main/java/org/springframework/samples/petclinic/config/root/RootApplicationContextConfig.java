package org.springframework.samples.petclinic.config.root;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.context.annotation.Import;
import org.springframework.samples.petclinic.util.CallMonitoringAspect;

@Configuration
@EnableMBeanExport
@EnableAspectJAutoProxy
@Import({BusinessConfig.class})
public class RootApplicationContextConfig {
	
	@Bean
    @Description("Call monitoring aspect that monitors call count and call invocation time")
	public CallMonitoringAspect callMonitor() {
		return new CallMonitoringAspect();
	}

}