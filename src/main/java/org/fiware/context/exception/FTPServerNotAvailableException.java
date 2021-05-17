package org.fiware.context.exception;

public class FTPServerNotAvailableException extends RuntimeException {
	public FTPServerNotAvailableException(String message) {
		super(message);
	}

	public FTPServerNotAvailableException(String message, Throwable cause) {
		super(message, cause);
	}
}
