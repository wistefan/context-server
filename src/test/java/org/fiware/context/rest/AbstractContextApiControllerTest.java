package org.fiware.context.rest;

import io.micronaut.context.annotation.Value;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import lombok.extern.slf4j.Slf4j;
import org.fiware.context.api.ContextServerApiTestClient;
import org.fiware.context.api.ContextServerApiTestSpec;
import org.fiware.context.model.ContextListVO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@Slf4j
abstract class AbstractContextApiControllerTest implements ContextServerApiTestSpec {

	protected static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	@BeforeEach
	public void setupBeforeTest() throws IOException {
		before();
	}

	@AfterEach
	public void cleanUpAfterTest() throws IOException {
		cleanup();
	}


	@Test
	@Override
	public void createContext201() throws Exception {
		HttpResponse<?> creationResponse = getTestClient().createContext(getCoreContextObject());
		assertEquals(HttpStatus.CREATED, creationResponse.getStatus(), "Creation should have been successful.");
		assertTrue(creationResponse.getHeaders().contains(HttpHeaders.LOCATION), "A location header should have been returned.");
		assertEquals(1, getPersistedContexts().size(), "The context should be in the cache.");
	}

	@Test
	@Override
	public void createContext400() throws Exception {
		String invalidContext = "Invalid context";
		ContextServerApiTestClient testClient = getTestClient();
		try {
			testClient.createContext(invalidContext);
			fail("The creation should not work for an invalid context.");
		} catch (HttpClientResponseException e) {
			assertEquals(HttpStatus.BAD_REQUEST, e.getStatus(), "Invalid context' should be rejected.");
		}
	}

	@Test
	@Override
	public void createContextWithId201() throws Exception {
		HttpResponse<?> creationResponse = getTestClient().createContextWithId("core-context.json", getCoreContextObject());
		assertEquals(HttpStatus.CREATED, creationResponse.getStatus(), "Creation should have been successful.");
		assertTrue(creationResponse.getHeaders().contains(HttpHeaders.LOCATION), "A location header should have been returned.");
		assertEquals("http://localhost:8080/jsonldContexts/core-context.json", creationResponse.getHeaders().get(HttpHeaders.LOCATION), "The correct header should have been provided.");
		assertEquals(1, getPersistedContexts().size(), "The context should be in the cache.");
	}

	@Test
	@Override
	public void createContextWithId400() throws Exception {
		String invalidContext = "Invalid context";
		ContextServerApiTestClient testClient = getTestClient();
		try {
			testClient.createContextWithId("invalid-context.json", invalidContext);
			fail("The creation should not work for an invalid context.");
		} catch (HttpClientResponseException e) {
			assertEquals(HttpStatus.BAD_REQUEST, e.getStatus(), "Invalid context' should be rejected.");
		}
	}

	@Test
	@Override
	public void createContextWithId409() throws Exception {
		initiateContextMap();
		ContextServerApiTestClient testClient = getTestClient();
		try {
			testClient.createContextWithId("core-context.json", getCoreContextObject());
			fail("The creation should not work for a duplicate context.");
		} catch (HttpClientResponseException e) {
			assertEquals(HttpStatus.CONFLICT, e.getStatus(), "The context should be reject with a conflict.");
		}
	}

	@Test
	@Override
	public void deleteContextById204() throws Exception {
		initiateContextMap();
		String coreContextId = "core-context.json";
		assertEquals(HttpStatus.NO_CONTENT, getTestClient().deleteContextById(coreContextId).getStatus(), "The status should have been deleted.");
		assertFalse(getPersistedContexts().containsKey(coreContextId), "The context should have been deleted.");
	}

	@Test
	@Override
	public void deleteContextById404() throws Exception {
		HttpResponse<?> response = getTestClient().deleteContextById("core-context.json");
		assertEquals(HttpStatus.NOT_FOUND, response.getStatus(), "Non existing contexts should cannot be deleted.");
	}

	@Test
	@Override
	public void getContextById200() throws Exception {
		initiateContextMap();
		HttpResponse<Object> response = getTestClient().getContextById("core-context.json");
		assertEquals(HttpStatus.OK, response.getStatus(), "The context should be retrieved.");
		assertEquals("max-age=31536000", response.getHeaders().get(HttpHeaders.CACHE_CONTROL), "The context should set the cache control header.");
		assertTrue(response.getBody().isPresent(), "Context should be present in the body.");
		assertEquals(getCoreContextObject(), response.getBody().get(), "Full core context should be returned.");
	}

	@Test
	@Override
	public void getContextById404() throws Exception {
		HttpResponse<Object> response = getTestClient().getContextById("core-context.json");
		assertEquals(HttpStatus.NOT_FOUND, response.getStatus(), "Requested context was not found.");
	}

	@Test
	@Override
	public void getContextList200() throws Exception {
		// empty
		HttpResponse<ContextListVO> response = getTestClient().getContextList();
		assertEquals(HttpStatus.OK, response.getStatus(), "Context List retrieval should be 200.l");
		assertTrue(response.getBody().isPresent(), "Empty List should be returned");
		assertTrue(response.getBody().get().isEmpty(), "Empty List should be returned");

		// retrieve something
		initiateContextMap();
		response = getTestClient().getContextList();
		assertEquals(HttpStatus.OK, response.getStatus(), "Context List retrieval should be 200.");
		assertTrue(response.getBody().isPresent(), "A list should be returned");
		ContextListVO contextListVO = response.getBody().get();
		assertNotNull(contextListVO, "The contexts should be retrieved.");
		assertEquals(2, contextListVO.size(), "Both contexts should have been returned");
		assertTrue(contextListVO.contains(URI.create("http://localhost:8080/jsonldContexts/data-models.json")), "Data models context should be provided.");
		assertTrue(contextListVO.contains(URI.create("http://localhost:8080/jsonldContexts/core-context.json")), "Core-context should be provided.");
	}

	protected abstract ContextServerApiTestClient getTestClient();

	protected abstract void initiateContextMap() throws Exception;

	protected abstract Map<String, Object> getPersistedContexts() throws IOException;

	protected abstract void before() throws IOException;

	protected abstract void cleanup() throws IOException;

	protected Object getCoreContextObject() throws IOException {
		InputStream coreInput = this.getClass().getClassLoader().getResourceAsStream("contexts/core-context.json");
		return OBJECT_MAPPER.readValue(coreInput, Object.class);
	}

	protected Object getDataModelsObject() throws IOException {
		InputStream dataModelsInput = this.getClass().getClassLoader().getResourceAsStream("contexts/data-models.json");
		return OBJECT_MAPPER.readValue(dataModelsInput, Object.class);
	}
}
