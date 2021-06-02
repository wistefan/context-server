package org.fiware.context.exception;

import lombok.Getter;

@Getter
public class ContextAlreadyExistsException extends RuntimeException {

	private final String id;

	public ContextAlreadyExistsException(String message, String id) {
		super(message);
		this.id = id;
	}

	public ContextAlreadyExistsException(String message, Throwable cause, String id) {
		super(message, cause);
		this.id = id;
	}
}
