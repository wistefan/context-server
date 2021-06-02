package org.fiware.context.configuration;

import io.micronaut.context.annotation.ConfigurationProperties;
import lombok.Data;
import lombok.ToString;

@ConfigurationProperties("ftp")
@ToString
@Data
public class FTPProperties {

	private boolean enabled = false;
	private String hostname;
	private int port = 21000;
	private String contextFolder = "/my-contexts";
	private boolean secured = false;
	private String username = "user";
	private String password = "password";
}
