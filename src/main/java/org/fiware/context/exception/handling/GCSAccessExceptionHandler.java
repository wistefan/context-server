package org.fiware.context.exception.handling;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.fiware.context.exception.GCSAccessException;

import javax.inject.Singleton;

/**
 * Handle exceptions to be thrown in case the gcs-bucket could not have been accessed.
 */
@Produces
@Singleton
@Requires(classes = {GCSAccessException.class, ExceptionHandler.class})
@Slf4j
public class GCSAccessExceptionHandler extends NGSICompliantExceptionHandler<GCSAccessException> {

	private static final ErrorType ASSOCIATED_ERROR = ErrorType.INTERNAL_ERROR;
	private static final String ERROR_TITLE = "Was not able to access GCS.";

	@Override
	public ErrorType getAssociatedErrorType() {
		return ASSOCIATED_ERROR;
	}

	@Override
	public HttpStatus getStatus() {
		return ASSOCIATED_ERROR.getStatus();
	}

	@Override
	public String getErrorTitle() {
		return ERROR_TITLE;
	}

	@Override
	public String getInstance(HttpRequest request, GCSAccessException exception) {
		return null;
	}
}
