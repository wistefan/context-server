package org.fiware.context.exception;

import lombok.Getter;

/**
 * Exception to be thrown in case a context-file is not readable.
 */
@Getter
public class FileNotReadableException extends RuntimeException {

	private final String contextId;

	public FileNotReadableException(String message, Throwable cause, String contextId) {
		super(message, cause);
		this.contextId = contextId;
	}
}
