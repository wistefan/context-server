package org.fiware.context.exception;

import lombok.Getter;

@Getter
public class FileNotReadableException extends RuntimeException {

	private final String contextId;

	public FileNotReadableException(String message, Throwable cause, String contextId) {
		super(message, cause);
		this.contextId = contextId;
	}
}
