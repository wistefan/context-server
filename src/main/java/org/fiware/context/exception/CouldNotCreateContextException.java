package org.fiware.context.exception;

/**
 * Exception to be thrown in case a context could not have been created.
 */
public class CouldNotCreateContextException extends RuntimeException {

	public CouldNotCreateContextException(String message) {
		super(message);
	}

	public CouldNotCreateContextException(String message, Throwable cause) {
		super(message, cause);
	}
}
