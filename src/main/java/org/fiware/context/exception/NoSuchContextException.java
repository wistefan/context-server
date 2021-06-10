package org.fiware.context.exception;

/**
 * Exception to be thrown in case the requested context does not exist.
 */
public class NoSuchContextException extends RuntimeException {

	public NoSuchContextException(String message) {
		super(message);
	}

}
