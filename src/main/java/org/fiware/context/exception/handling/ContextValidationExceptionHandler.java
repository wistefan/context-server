package org.fiware.context.exception.handling;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.fiware.context.exception.ContextValidationException;

import javax.inject.Singleton;

@Produces
@Singleton
@Requires(classes = {ContextValidationException.class, ExceptionHandler.class})
@Slf4j
public class ContextValidationExceptionHandler extends NGSICompliantExceptionHandler<ContextValidationException>{

	private static final ErrorType ASSOCIATED_ERROR = ErrorType.BAD_REQUEST_DATA;
	private static final String ERROR_TITLE = "Invalid context received.";

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
	public String getInstance(HttpRequest request, ContextValidationException exception) {
		return null;
	}
}
