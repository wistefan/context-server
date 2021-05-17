package org.fiware.context.rest;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.fiware.context.api.ContextServerApiTestClient;
import org.fiware.context.api.ContextServerApiTestSpec;
import org.fiware.context.model.ContextListVO;
import org.fiware.context.storage.ContextMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest(environments = "application-test-in-memory")
public class ContextApiControllerInMemoryTest implements ContextServerApiTestSpec {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	@Inject
	private ContextMap contextMap;

	@Inject
	private ContextServerApiTestClient testClient;

	@Override
	public void createContext201() throws Exception {

	}

	@Override
	public void createContext400() throws Exception {

	}

	@Override
	public void createContextWithId201() throws Exception {

	}

	@Override
	public void createContextWithId400() throws Exception {

	}

	@Override
	public void createContextWithId409() throws Exception {

	}

	@Override
	public void deleteContextById204() throws Exception {

	}

	@Override
	public void deleteContextById400() throws Exception {

	}

	@Override
	public void deleteContextById404() throws Exception {

	}

	@Test
	@Override
	public void getContextById200() throws Exception {
		initiateContextMap();
		HttpResponse<Object> response = testClient.getContextById("core-context.json");
		assertEquals(HttpStatus.OK, response.getStatus(), "The context should be retrieved.");
		assertEquals(response.getHeaders().get(HttpHeaders.CACHE_CONTROL), "max-age=31536000", "The context should set the cache control header.");
		assertTrue(response.getBody().isPresent(), "Context should be present in the body.");
		assertEquals(getCoreContextObject(), response.getBody().get(), "Full core context should be returned.");
	}

	@Override
	public void getContextById400() throws Exception {

	}

	@Override
	public void getContextById404() throws Exception {

	}

	@Test
	@Override
	public void getContextList200() throws Exception {
		// empty
		HttpResponse<ContextListVO> response = testClient.getContextList();
		assertEquals(HttpStatus.OK, response.getStatus(), "Context List retrieval should be 200.l");
		assertTrue(response.getBody().isPresent(), "Empty List should be returned");
		assertTrue(response.getBody().get().isEmpty(), "Empty List should be returned");

		// retrieve something
		initiateContextMap();
		response = testClient.getContextList();
		assertEquals(HttpStatus.OK, response.getStatus(), "Context List retrieval should be 200.");
		assertTrue(response.getBody().isPresent(), "A list should be returned");
		ContextListVO contextListVO = response.getBody().get();
		assertNotNull(contextListVO, "The contexts should be retrieved.");
		assertEquals(2, contextListVO.size(), "Both contexts should have been returned");
		assertTrue(contextListVO.contains(URI.create("http://localhost:8080/jsonldContexts/data-models.json")), "Data models context should be provided.");
		assertTrue(contextListVO.contains(URI.create("http://localhost:8080/jsonldContexts/core-context.json")), "Core-context should be provided.");
	}

	private void initiateContextMap() throws IOException {
		contextMap.put("core-context.json", getCoreContextObject());
		contextMap.put("data-models.json", getDataModelsObject());
	}

	private Object getCoreContextObject() throws IOException {
		InputStream coreInput = this.getClass().getClassLoader().getResourceAsStream("contexts/core-context.json");
		return OBJECT_MAPPER.readValue(coreInput, Object.class);
	}

	private Object getDataModelsObject() throws IOException {
		InputStream dataModelsInput = this.getClass().getClassLoader().getResourceAsStream("contexts/data-models.json");
		return OBJECT_MAPPER.readValue(dataModelsInput, Object.class);
	}

}
