package org.fiware.context.exception;

/**
 * Exception to be thrown in case the context folder is not readable.
 */
public class FolderNotReadableException extends RuntimeException {

	public FolderNotReadableException(String message, Throwable cause) {
		super(message, cause);
	}
}
