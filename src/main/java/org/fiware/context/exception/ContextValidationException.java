package org.fiware.context.exception;

/**
 * Exception to be thrown if a context was received that is invalid.
 */
public class ContextValidationException extends RuntimeException {

	public ContextValidationException(String message, Throwable cause) {
		super(message, cause);
	}
}
