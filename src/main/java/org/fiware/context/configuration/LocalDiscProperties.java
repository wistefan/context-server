package org.fiware.context.configuration;

import io.micronaut.context.annotation.ConfigurationProperties;
import lombok.Data;

/**
 * Use the local file system as storage backend
 */
@ConfigurationProperties("local")
@Data
public class LocalDiscProperties {

	/**
	 * Should local storage be enabled.
	 */
	private boolean enabled = false;
	/**
	 * The folder to store the contexts at.
	 */
	private String contextFolder = "/ld-contexts";

}
