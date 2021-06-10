package org.fiware.context.exception.handling;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.fiware.context.exception.NoSuchContextException;

import javax.inject.Singleton;

/**
 * Handle exceptions to be thrown in case the requested context does not exist.
 */
@Produces
@Singleton
@Requires(classes = {NoSuchContextException.class, ExceptionHandler.class})
@Slf4j
public class NoSuchContextExceptionHandler extends NGSICompliantExceptionHandler<NoSuchContextException> {

	private static final ErrorType ASSOCIATED_ERROR = ErrorType.RESOURCE_NOT_FOUND;
	private static final String ERROR_TITLE = "Context does not exist.";

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
	public String getInstance(HttpRequest request, NoSuchContextException exception) {
		return null;
	}
}
