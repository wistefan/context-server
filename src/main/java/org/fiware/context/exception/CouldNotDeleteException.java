package org.fiware.context.exception;

import lombok.Getter;

@Getter
public class CouldNotDeleteException extends RuntimeException {

	private final String contextId;

	public CouldNotDeleteException(String message, String contextId) {
		super(message);
		this.contextId = contextId;
	}

	public CouldNotDeleteException(String message, Throwable cause, String contextId) {
		super(message, cause);
		this.contextId = contextId;
	}
}
