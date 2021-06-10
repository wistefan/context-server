package org.fiware.context.exception.handling;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.fiware.context.exception.FTPServerNotAvailableException;

import javax.inject.Singleton;

/**
 *
 * Handle exceptions to be thrown in case the ftp is not available.
 */
@Produces
@Singleton
@Requires(classes = {FTPServerNotAvailableException.class, ExceptionHandler.class})
@Slf4j
public class FTPServerNotAvailableExceptionHandler extends NGSICompliantExceptionHandler<FTPServerNotAvailableException> {

	private static final ErrorType ASSOCIATED_ERROR = ErrorType.INTERNAL_ERROR;
	private static final String ERROR_TITLE = "FTP Server not available.";

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
	public String getInstance(HttpRequest request, FTPServerNotAvailableException exception) {
		return null;
	}
}
