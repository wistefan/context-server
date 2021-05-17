package org.fiware.context.rest;

import com.apicatalog.jsonld.JsonLd;
import com.apicatalog.jsonld.JsonLdError;
import com.apicatalog.jsonld.document.JsonDocument;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fiware.context.api.ContextServerApi;
import org.fiware.context.configuration.GeneralProperties;
import org.fiware.context.exception.ContextValidationException;
import org.fiware.context.exception.CouldNotCreateContextException;
import org.fiware.context.exception.CouldNotCreateContextURLException;
import org.fiware.context.model.ContextListVO;
import org.fiware.context.storage.ContextRepository;

import javax.validation.constraints.NotNull;
import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Controller
public class ContextApiController implements ContextServerApi {

	private static final String CACHE_CONTROL_HEADER_TEMPLATE = "max-age=%s";
	private static final String CONTEXT_URL_TEMPLATE = "%s/jsonldContexts/%s";

	private final GeneralProperties generalProperties;
	private final ContextRepository contextRepository;
	private final ObjectMapper objectMapper;

	@Override
	public HttpResponse<Object> createContext(@NotNull Object body) {
		validateContext(body);
		Optional<String> id = contextRepository.createContext(body);
		URL contextURL = id
				.map(this::getContextUrl)
				.orElseThrow(() -> new CouldNotCreateContextException("Was not able to create the requested context."));
		return HttpResponse.created(contextURL);
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
			return HttpResponse.notFound();
		}
	}

	@Override
	public Optional<ContextListVO> getContextList() {
		ContextListVO contextListVO = new ContextListVO();
		contextRepository.getContextList().stream().map(this::getContextURI).forEach(contextListVO::add);
		return Optional.of(contextListVO);
	}

	@Override
	public HttpResponse<Object> createContextWithId(String contextId, @NotNull Object body) {
		validateContext(body);
		Optional<String> id = contextRepository.createContextWithId(contextId, body);
		URL contextURL = id
				.map(this::getContextUrl)
				.orElseThrow(() -> new CouldNotCreateContextException("Was not able to create the requested context."));
		return HttpResponse.created(contextURL);
	}

	private void validateContext(Object ldContext) {
		try {
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(objectMapper.writeValueAsBytes(ldContext));
			JsonDocument.of(byteArrayInputStream);
		} catch (JsonProcessingException | JsonLdError e) {
		 	throw new ContextValidationException("The provided context is not valid.", e);
		}
	}

	private URI getContextURI(String id) {
		try {
			return new URI(String.format(CONTEXT_URL_TEMPLATE, generalProperties.getBaseUrl(), id));
		} catch (URISyntaxException e) {
			throw new CouldNotCreateContextURLException(String.format("Was not able to create context url for %s.", id), e);
		}
	}

	private URL getContextUrl(String id) {
		try {
			return new URL(String.format(CONTEXT_URL_TEMPLATE, generalProperties.getBaseUrl(), id));
		} catch (MalformedURLException e) {
			throw new CouldNotCreateContextURLException(String.format("Was not able to create context url for %s.", id), e);
		}
	}

}
