package org.fiware.context.rest;


import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.fiware.context.api.ContextServerApiTestClient;
import org.fiware.context.exception.FileNotReadableException;
import org.fiware.context.exception.GCSAccessException;
import org.fiware.context.exception.handling.ProblemDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import javax.inject.Inject;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@MicronautTest(environments = "test-gcs")
public class ContextApiControllerGCSErrorTest {

	@Inject
	private Storage storage;

	@Inject
	private ContextServerApiTestClient testClient;


	@MockBean
	@Replaces(bean = Storage.class)
	public Storage storage() {
		Storage storage = mock(Storage.class);
		// enable initialization
		Page<Blob> blobPage = mock(Page.class);
		Iterable<Blob> iterableBlob = List.of();
		when(blobPage.iterateAll()).thenReturn(iterableBlob);
		when(storage.list(anyString())).thenReturn(blobPage);

		return storage;
	}


	@DisplayName("Creation should fail with inaccessible bucket")
	@Test
	public void createWithIdFail() throws Throwable {

		when(storage.readAllBytes(any())).thenThrow(new StorageException(404, "Not found"));
		when(storage.create(any(BlobInfo.class), any(byte[].class))).thenThrow(new StorageException(403, "Not accessible"));

		assert500(() -> testClient.createContextWithId("contextId", "{\"testContext\":\"t\"}"));
	}

	@DisplayName("Creation should fail with inaccessible bucket")
	@Test
	public void createWithIdFailOnList() throws Throwable {
		when(storage.readAllBytes(any())).thenThrow(new StorageException(403, "Not accessible"));

		assert500(() -> testClient.createContextWithId("contextId", "{\"testContext\":\"t\"}"));
	}

	@DisplayName("Context retrieval should fail with invalid json in bucket")
	@Test
	public void getContextFailOnJson() throws Throwable {
		byte[] b = new byte[20];
		new Random().nextBytes(b);
		when(storage.readAllBytes(any())).thenReturn(b);

		assert500(() -> testClient.getContextById("contextId"));
	}

	@DisplayName("Context retrieval should fail with inaccessible bucket")
	@Test
	public void getContextFailOnBucket() throws Throwable {

		when(storage.readAllBytes(any())).thenThrow(new StorageException(403, "Not accessible"));
		assert500(() -> testClient.getContextById("contextId"));
	}

	@DisplayName("Context list retrieval should fail with inaccessible bucket")
	@Test
	public void getContextListFailOnBucket() throws Throwable {

		when(storage.list(anyString())).thenThrow(new StorageException(403, "Not accessible"));

		assert500(() -> testClient.getContextList());
	}

	@DisplayName("Context deletion should fail with inaccessible bucket")
	@Test
	public void getContextDeleteFailOnBucket() throws Throwable {

		when(storage.readAllBytes(any())).thenReturn(new ObjectMapper().writeValueAsBytes("{\"test\":\"t\"}"));
		when(storage.delete(any(BlobId.class))).thenThrow(new StorageException(403, "Not accessible"));

		assert500(() -> testClient.deleteContextById("context"));
	}

	private void assert500(Executable executable) throws Throwable {
		try {
			executable.execute();
			fail("GCS problems should be a 500");
		} catch (HttpClientResponseException e) {
			assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, e.getStatus(), "GCS problems should be a 500");
		}
	}
}
