package org.fiware.context.exception;

/**
 * Exception to be thrown in case the gcs-bucket could not have been accessed.
 */
public class GCSAccessException extends RuntimeException {

	public GCSAccessException(String message, Throwable cause) {
		super(message, cause);
	}
}
