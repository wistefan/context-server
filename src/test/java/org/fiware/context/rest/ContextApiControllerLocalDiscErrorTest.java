package org.fiware.context.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.fiware.context.api.ContextServerApiTestClient;
import org.fiware.context.storage.LocalDiscPersistence;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@MicronautTest(environments = "test-local")
public class ContextApiControllerLocalDiscErrorTest {


	@Inject
	private ContextServerApiTestClient testClient;

	@Inject
	private LocalDiscPersistence localDiscPersistence;

	@Inject
	private ObjectMapper objectMapper;

	@MockBean
	@Replaces(LocalDiscPersistence.class)
	public LocalDiscPersistence mockLocalDisk() {
		return mock(LocalDiscPersistence.class);
	}

	@MockBean
	@Replaces(ObjectMapper.class)
	public ObjectMapper mockObjectMapper() {
		return spy(ObjectMapper.class);
	}

	@DisplayName("Context deletion should fail on IO-exists-error.")
	@Test
	public void deleteContextFailOnExist() throws Throwable {
		when(localDiscPersistence.exists(any())).thenThrow(new RuntimeException("Not able to access disc."));
		assert500(() -> testClient.deleteContextById("myContext.jason"));
	}

	@DisplayName("Context deletion should fail on IO-delete-error.")
	@Test
	public void deleteContextFailOnDelete() throws Throwable {
		when(localDiscPersistence.exists(any())).thenReturn(true);
		doThrow(new IOException("Not able to access disc.")).when(localDiscPersistence).deleteFile(any());
		assert500(() -> testClient.deleteContextById("myContext.jason"));
	}

	@DisplayName("Get context should fail on IO.")
	@Test
	public void getContextFailIO() throws Throwable {
		when(localDiscPersistence.readString(any())).thenThrow(new IOException("Not able to access disc."));
		assert500(() -> testClient.getContextById("myContext.json"));
	}

	@DisplayName("Get context list should fail on IO.")
	@Test
	public void getContextListFailIO() throws Throwable {
		when(localDiscPersistence.list(any())).thenThrow(new IOException("Not able to access disc."));
		assert500(() -> testClient.getContextList());
	}

	@DisplayName("Create context by ID should fail on IO.")
	@Test
	public void createContextByIdFailIO() throws Throwable {
		doThrow(new IOException("Not able to access disc.")).when(objectMapper).writeValue(any(File.class), any(Object.class));
		when(localDiscPersistence.exists(any())).thenReturn(false);
		assert500(() -> testClient.createContextWithId("myContext", "{\"test\":\"context\"}"));
	}

	@DisplayName("Create context should fail on IO.")
	@Test
	public void createContextFailIO() throws Throwable {
		doThrow(new IOException("Not able to access disc.")).when(objectMapper).writeValue(any(File.class), any(Object.class));
		when(localDiscPersistence.exists(any())).thenReturn(false);
		assert500(() -> testClient.createContext("{\"test\":\"context\"}"));
	}


	private void assert500(Executable executable) throws Throwable {
		try {
			executable.execute();
			fail("IO problems should be a 500");
		} catch (HttpClientResponseException e) {
			assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, e.getStatus(), "IO problems should be a 500");
		}
	}
}
