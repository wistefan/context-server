package org.fiware.context.configuration;

import io.micronaut.context.annotation.ConfigurationProperties;
import lombok.Data;

@ConfigurationProperties("general")
@Data
public class GeneralProperties {

	private String baseUrl;
	// Used for the cache-control header, maximum age is set to the max of 1 year in seconds
	private Integer maxAge = 31536000;
}
