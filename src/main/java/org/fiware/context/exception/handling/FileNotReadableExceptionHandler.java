package org.fiware.context.exception.handling;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.fiware.context.exception.FileNotReadableException;

import javax.inject.Singleton;

/**
 * Handle exceptions to be thrown in case a context-file is not readable.
 */
@Produces
@Singleton
@Requires(classes = {FileNotReadableException.class, ExceptionHandler.class})
@Slf4j
public class FileNotReadableExceptionHandler extends NGSICompliantExceptionHandler<FileNotReadableException> {

	private static final ErrorType ASSOCIATED_ERROR = ErrorType.INTERNAL_ERROR;
	private static final String ERROR_TITLE = "Was not able to read context-file.";

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
	public String getInstance(HttpRequest request, FileNotReadableException exception) {
		return exception.getContextId();
	}
}
