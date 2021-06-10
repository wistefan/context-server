package org.fiware.context.exception;

/**
 *  Exception to be thrown in case a url for the context could not have been created.
 */
public class CouldNotCreateContextURLException extends RuntimeException {

	public CouldNotCreateContextURLException(String message, Throwable cause) {
		super(message, cause);
	}
}
