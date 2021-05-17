package org.fiware.context.configuration;

import io.micronaut.context.annotation.ConfigurationProperties;
import lombok.Data;
import lombok.ToString;

@ConfigurationProperties("gcs")
@Data
@ToString
public class GCSProperties {

	private boolean enabled = false;
	private String bucketName ="my-contexts";
}
