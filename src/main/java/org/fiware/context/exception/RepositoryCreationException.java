package org.fiware.context.exception;

public class RepositoryCreationException extends Exception {

	public RepositoryCreationException(String message) {
		super(message);
	}

	public RepositoryCreationException(String message, Throwable cause) {
		super(message, cause);
	}
}
