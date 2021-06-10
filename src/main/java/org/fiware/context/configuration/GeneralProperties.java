package org.fiware.context.configuration;

import io.micronaut.context.annotation.ConfigurationProperties;
import lombok.Data;

/**
 * General properties to be used for the server.
 */
@ConfigurationProperties("general")
@Data
public class GeneralProperties {

	/**
	 * Baseurl of the server to be used for creating the location header on request.
	 */
	private String baseUrl;
	/**
	 * Configure max age for chache control. Default is set to the max of 1 year(in s)
 	 */
	private Integer maxAge = 31536000;
}
