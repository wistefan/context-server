package org.fiware.context.exception.handling;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;

/**
 * Handler to catch all not specifically(unexpected) handled exceptions and map them NGSI-LD compliant.
 */
@Produces
@Singleton
@Requires(classes = {Exception.class, ExceptionHandler.class})
@Slf4j
public class GeneralExceptionHandler extends NGSICompliantExceptionHandler<Exception> {

	private static final ErrorType ASSOCIATED_ERROR = ErrorType.INTERNAL_ERROR;
	private static final String ERROR_TITLE = "Unexpected error.";

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
	public String getInstance(HttpRequest request, Exception exception) {
		return null;
	}

}
