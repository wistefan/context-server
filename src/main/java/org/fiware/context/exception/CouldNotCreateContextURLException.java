package org.fiware.context.exception;

public class CouldNotCreateContextURLException extends RuntimeException {

	public CouldNotCreateContextURLException(String message) {
		super(message);
	}

	public CouldNotCreateContextURLException(String message, Throwable cause) {
		super(message, cause);
	}
}
