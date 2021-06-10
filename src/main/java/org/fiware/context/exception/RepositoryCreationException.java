package org.fiware.context.exception;

/**
 * Exception to be thrown in case a repository could not have been created.
 */
public class RepositoryCreationException extends Exception {

	public RepositoryCreationException(String message, Throwable cause) {
		super(message, cause);
	}
}
