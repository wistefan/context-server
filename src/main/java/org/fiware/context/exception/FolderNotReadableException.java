package org.fiware.context.exception;

public class FolderNotReadableException extends RuntimeException {

	public FolderNotReadableException(String message) {
		super(message);
	}

	public FolderNotReadableException(String message, Throwable cause) {
		super(message, cause);
	}
}
