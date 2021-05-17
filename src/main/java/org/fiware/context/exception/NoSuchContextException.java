package org.fiware.context.exception;

public class NoSuchContextException extends RuntimeException {

	public NoSuchContextException(String message) {
		super(message);
	}

	public NoSuchContextException(String message, Throwable cause) {
		super(message, cause);
	}
}
