package org.fiware.context.configuration;

import io.micronaut.context.annotation.ConfigurationProperties;
import lombok.Data;
import lombok.ToString;

/**
 * Configurations for running the server with an FTP-Server as storage backend.
 */
@ConfigurationProperties("ftp")
@ToString
@Data
public class FTPProperties {

	/**
	 * Should ftp be enabled.
	 */
	private boolean enabled = false;
	/**
	 * Hostname of the ftp-server
	 */
	private String hostname;
	/**
	 * Port to contact the ftp-server at.
	 */
	private int port = 21000;
	/**
	 * Folder to store the contexts in the ftp.
	 */
	private String contextFolder = "/my-contexts";
	/**
	 * Is the ftp-server requiring authentication?
	 */
	private boolean secured = false;
	/**
	 * Username to authenticate at the ftp.
	 */
	private String username = "user";
	/**
	 * Password to authenticate at the ftp.
	 */
	private String password = "password";
}
