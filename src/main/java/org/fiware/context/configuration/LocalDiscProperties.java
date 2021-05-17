package org.fiware.context.configuration;

import io.micronaut.context.annotation.ConfigurationProperties;
import lombok.Data;

@ConfigurationProperties("local")
@Data
public class LocalDiscProperties {

	private boolean enabled = false;
	private String contextFolder = "/ld-contexts";

}
