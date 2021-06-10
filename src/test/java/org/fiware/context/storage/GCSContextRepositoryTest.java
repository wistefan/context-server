package org.fiware.context.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import org.fiware.context.configuration.GCSProperties;
import org.fiware.context.exception.FileNotReadableException;
import org.fiware.context.exception.GCSAccessException;
import org.fiware.context.exception.RepositoryCreationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;



class GCSContextRepositoryTest {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	private GCSProperties gcsProperties;
	private Storage storage;
	private GCSContextRepository gcsContextRepository;

	@BeforeEach
	private void setUp() {
		gcsProperties = new GCSProperties();
		storage = mock(Storage.class);
		gcsContextRepository = new GCSContextRepository(OBJECT_MAPPER, gcsProperties, storage);
	}

	@DisplayName("Check successful initialization")
	@Test
	void init() throws Exception {
		Page<Blob> blobPage = mock(Page.class);
		Iterable<Blob> iterableBlob = List.of();
		when(blobPage.iterateAll()).thenReturn(iterableBlob);
		when(storage.list(anyString())).thenReturn(blobPage);

		assertDoesNotThrow(() -> gcsContextRepository.init(), "The repository should initialize successfully.");
	}

	@DisplayName("Check initialization fails when bucket not accessible.")
	@Test
	void initFail() throws Exception {
		when(storage.list(anyString())).thenThrow(new StorageException(123, "Not accessible"));

		assertThrows(RepositoryCreationException.class, () -> gcsContextRepository.init(), "The repository should not initialize successfully.");
	}
}