package org.fiware.context.storage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageOptions;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.Requires;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fiware.context.configuration.GCSProperties;
import org.fiware.context.exception.CouldNotCreateContextException;
import org.fiware.context.exception.FileNotReadableException;
import org.fiware.context.exception.RepositoryCreationException;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
// context scoped, to enable startup connectivity check
@Context
@Requires(property = "gcs.enabled", value = "true")
@RequiredArgsConstructor
public class GCSContextRepository implements ContextRepository {

	private final ObjectMapper objectMapper;
	private final GCSProperties gcsProperties;
	private final Storage storage = StorageOptions.getDefaultInstance().getService();

	@PostConstruct
	public void init() throws RepositoryCreationException{
		try {
			// list the context to check bucket is accessible
			getContextList();
		} catch (Exception e) {
			throw new RepositoryCreationException(String.format("Was not able to initialize GCS repository with config: %s", gcsProperties), e);
		}
		log.info("GCS repository successfully started.");
	}

	@Override
	public Optional<String> createContext(Object ldContext) {
		String contextId = UUID.randomUUID().toString();
		return persistContextWithId(contextId, ldContext);
	}

	private Optional<String> persistContextWithId(String contextId, Object ldContext) {
		BlobInfo blobInfo = BlobInfo.newBuilder(getBlobId(contextId)).setContentType("text/plain").build();
		try {
			storage.create(blobInfo, objectMapper.writeValueAsBytes(ldContext));
		} catch (JsonProcessingException e) {
			throw new CouldNotCreateContextException("Was not able to create the context.", e);
		} catch (StorageException e) {
			//TODO: Handle all of them
		}
		return Optional.of(contextId);
	}

	@Override
	public Optional<String> createContextWithId(String id, Object ldContext) {
		if (getContext(id).isPresent()) {
			log.warn("Context with id {} already exists.", id);
			return Optional.empty();
		}
		return persistContextWithId(id, ldContext);
	}

	@Override
	public void deleteContext(String id) {
		storage.delete(getBlobId(id));
	}

	@Override
	public Optional<Object> getContext(String id) {
		BlobId blobId = getBlobId(id);
		byte[] content = storage.readAllBytes(blobId);
		String contextString = new String(content, UTF_8);
		try {
			return Optional.of(objectMapper.readValue(contextString, Object.class));
		} catch (JsonProcessingException e) {
			throw new FileNotReadableException(String.format("Was not able to read file for context %s from bucket %s.", id, gcsProperties.getBucketName()), e);
		}
	}

	@Override
	public List<String> getContextList() {
		List<String> contextList = new ArrayList<>();
		for (Blob contextBlob : storage.list(gcsProperties.getBucketName()).iterateAll()) {
			contextList.add(contextBlob.getName());
		}
		return contextList;
	}

	private BlobId getBlobId(String id) {
		return BlobId.of(gcsProperties.getBucketName(), id);
	}


}
