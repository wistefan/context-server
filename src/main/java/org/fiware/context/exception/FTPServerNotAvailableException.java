package org.fiware.context.exception;

/**
 * Exception to be thrown in case the ftp is not available.
 */
public class FTPServerNotAvailableException extends RuntimeException {

	public FTPServerNotAvailableException(String message) {
		super(message);
	}

	public FTPServerNotAvailableException(String message, Throwable cause) {
		super(message, cause);
	}
}
