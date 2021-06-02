package org.fiware.context.exception;

public class GCSAccessException extends RuntimeException {

	public GCSAccessException(String message) {
		super(message);
	}

	public GCSAccessException(String message, Throwable cause) {
		super(message, cause);
	}
}
