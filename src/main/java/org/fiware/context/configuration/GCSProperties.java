package org.fiware.context.configuration;

import io.micronaut.context.annotation.ConfigurationProperties;
import lombok.Data;
import lombok.ToString;

/**
 * Configurations for running the server with a GCS-Bucket as storage backend.
 */
@ConfigurationProperties("gcs")
@Data
@ToString
public class GCSProperties {

	/**
	 * Should gcs storage be enabled.
	 */
	private boolean enabled = false;
	/**
	 * Name of the bucket to be used.
	 */
	private String bucketName ="my-contexts";
}
