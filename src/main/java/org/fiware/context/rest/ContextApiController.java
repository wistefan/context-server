package org.fiware.context.rest;

import com.apicatalog.jsonld.JsonLdError;
import com.apicatalog.jsonld.document.JsonDocument;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fiware.context.api.ContextServerApi;
import org.fiware.context.configuration.GeneralProperties;
import org.fiware.context.exception.ContextValidationException;
import org.fiware.context.exception.CouldNotCreateContextException;
import org.fiware.context.exception.CouldNotCreateContextURLException;
import org.fiware.context.exception.NoSuchContextException;
import org.fiware.context.model.ContextListVO;
import org.fiware.context.storage.ContextRepository;

import javax.validation.constraints.NotNull;
import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;

/**
 * Rest controller for handling all context requests.
 */
@Slf4j
@RequiredArgsConstructor
@Controller
public class ContextApiController implements ContextServerApi {

	/**
	 * Template to be used for the cache control header
	 */
	private static final String CACHE_CONTROL_HEADER_TEMPLATE = "max-age=%s";
	/**
	 * Template for the context-url to be returned.
	 */
	private static final String CONTEXT_URL_TEMPLATE = "%s/jsonldContexts/%s";

	/**
	 * General properties to be used.
	 */
	private final GeneralProperties generalProperties;
	/**
	 * Repository containing the contexts.
	 */
	private final ContextRepository contextRepository;
	/**
	 * ObjectMapper to be used.
	 */
	private final ObjectMapper objectMapper;

	@Override
	public HttpResponse<Object> createContext(@NotNull Object body) {
		validateContext(body);
		Optional<String> id = contextRepository.createContext(body);
		URL contextURL = id
				.map(this::getContextUrl)
				.orElseThrow(() -> new CouldNotCreateContextException("Was not able to create the requested context."));
		return HttpResponse.status(HttpStatus.CREATED).header(HttpHeaders.LOCATION, contextURL.toString());
	}

	@Override
	public void deleteContextById(String contextId) {
		contextRepository.deleteContext(contextId);
	}

	@Override
	public HttpResponse<Object> getContextById(String contextId) {
		Optional<Object> optionalContext = contextRepository.getContext(contextId);
		if (optionalContext.isPresent()) {
			return HttpResponse.ok(optionalContext.get())
					.header(HttpHeaders.CACHE_CONTROL, String.format(CACHE_CONTROL_HEADER_TEMPLATE, generalProperties.getMaxAge()));
		} else {
			throw new NoSuchContextException("Requested context was not available.");
		}
	}

	@Override
	public ContextListVO getContextList() {
		ContextListVO contextListVO = new ContextListVO();
		contextRepository.getContextList().stream().map(this::getContextURI).forEach(contextListVO::add);
		return contextListVO;
	}

	@Override
	public HttpResponse<Object> createContextWithId(String contextId, @NotNull Object body) {
		validateContext(body);
		Optional<String> id = contextRepository.createContextWithId(contextId, body);
		URL contextURL = id
				.map(this::getContextUrl)
				.orElseThrow(() -> new CouldNotCreateContextException("Was not able to create the requested context."));
		return HttpResponse.status(HttpStatus.CREATED).header(HttpHeaders.LOCATION, contextURL.toString());
	}

	/**
	 * Validate if the object is valid json.
	 *
	 * @param ldContext - the object to be validated
	 */
	private void validateContext(Object ldContext) {
		try {
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(objectMapper.writeValueAsBytes(ldContext));
			JsonDocument.of(byteArrayInputStream);
		} catch (JsonProcessingException | JsonLdError e) {
			throw new ContextValidationException("The provided context is not valid.", e);
		}
	}

	/**
	 * Generate a context-uri from the give id
	 *
	 * @param id - id to generate the uri for
	 * @return the generated URI
	 */
	private URI getContextURI(String id) {
		try {
			return new URI(String.format(CONTEXT_URL_TEMPLATE, generalProperties.getBaseUrl(), id));
		} catch (URISyntaxException e) {
			throw new CouldNotCreateContextURLException(String.format("Was not able to create context url for %s.", id), e);
		}
	}

	/**
	 * Generate a context-url from the give id
	 *
	 * @param id - id to generate the ul for
	 * @return the generated URl
	 */
	private URL getContextUrl(String id) {
		try {
			return new URL(String.format(CONTEXT_URL_TEMPLATE, generalProperties.getBaseUrl(), id));
		} catch (MalformedURLException e) {
			throw new CouldNotCreateContextURLException(String.format("Was not able to create context url for %s.", id), e);
		}
	}

}
