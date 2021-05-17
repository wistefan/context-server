package org.fiware.context.exception;

public class CouldNotCreateContextException extends RuntimeException {

	public CouldNotCreateContextException(String message) {
		super(message);
	}

	public CouldNotCreateContextException(String message, Throwable cause) {
		super(message, cause);
	}
}
