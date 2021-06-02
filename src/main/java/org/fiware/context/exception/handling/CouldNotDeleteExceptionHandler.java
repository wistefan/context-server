package org.fiware.context.exception.handling;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.fiware.context.exception.CouldNotCreateContextException;
import org.fiware.context.exception.CouldNotDeleteException;

import javax.inject.Singleton;

@Produces
@Singleton
@Requires(classes = {CouldNotDeleteException.class, ExceptionHandler.class})
@Slf4j
public class CouldNotDeleteExceptionHandler extends NGSICompliantExceptionHandler<CouldNotDeleteException> {

	private static final ErrorType ASSOCIATED_ERROR = ErrorType.INTERNAL_ERROR;
	private static final String ERROR_TITLE = "Was not able to delete context";

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
	public String getInstance(HttpRequest request, CouldNotDeleteException exception) {
		return exception.getContextId();
	}
}
