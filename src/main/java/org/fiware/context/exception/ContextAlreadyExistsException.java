package org.fiware.context.exception;

import lombok.Getter;

/**
 * Exception to be thrown when a context was tried to be created that already existed.
 */
@Getter
public class ContextAlreadyExistsException extends RuntimeException {

	private final String id;

	public ContextAlreadyExistsException(String message, String id) {
		super(message);
		this.id = id;
	}

}
