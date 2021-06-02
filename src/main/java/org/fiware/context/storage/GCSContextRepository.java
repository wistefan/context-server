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
import org.fiware.context.exception.GCSAccessException;
import org.fiware.context.exception.NoSuchContextException;
import org.fiware.context.exception.RepositoryCreationException;
import org.fiware.context.exception.ContextAlreadyExistsException;

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
	private final Storage storage;

	@PostConstruct
	public void init() throws RepositoryCreationException {
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

		if (blobExists(contextId)) {
			log.warn("Context with id {} already exists.", contextId);
			throw new ContextAlreadyExistsException(String.format("The context %s already exists.", contextId), contextId);
		}
		BlobInfo blobInfo = BlobInfo.newBuilder(getBlobId(contextId)).setContentType("text/plain").build();
		try {
			storage.create(blobInfo, objectMapper.writeValueAsBytes(ldContext));
		} catch (JsonProcessingException e) {
			throw new CouldNotCreateContextException("Was not able to create the context.", e);
		} catch (StorageException e) {
			throw new GCSAccessException(String.format("Was not able to access bucket %s", getBlobId(contextId)), e);
		}
		return Optional.of(contextId);
	}

	@Override
	public Optional<String> createContextWithId(String id, Object ldContext) {
		return persistContextWithId(id, ldContext);
	}

	@Override
	public void deleteContext(String id) {
		if(!blobExists(id)) {
			throw new NoSuchContextException(String.format("Context %s does not exist.", id));
		}
		try {
			storage.delete(getBlobId(id));
		} catch (StorageException e) {
			throw new GCSAccessException(String.format("Was not able to access bucket %s", getBlobId(id)), e);
		}
	}

	@Override
	public Optional<Object> getContext(String id) {
		byte[] content = getBytesFromBlob(id);
		String contextString = new String(content, UTF_8);
		try {
			return Optional.of(objectMapper.readValue(contextString, Object.class));
		} catch (JsonProcessingException e) {
			throw new FileNotReadableException(String.format("Was not able to read file for context %s from bucket %s.", id, gcsProperties.getBucketName()), e, id);
		} catch (StorageException e) {
			throw new GCSAccessException(String.format("Was not able to access bucket %s", getBlobId(id)), e);
		}
	}

	private boolean blobExists(String id) {
		try {
			getBytesFromBlob(id);
			return true;
		}catch (NoSuchContextException e) {
			return false;
		}
	}

	private byte[] getBytesFromBlob(String id) {
		BlobId blobId = getBlobId(id);
		try {
			byte[] content = storage.readAllBytes(blobId);
			return content;
		} catch (StorageException e) {
			if (e.getCode() == 404) {
				throw new NoSuchContextException(String.format("Context %s does not exist.", id));
			} else {
				throw new GCSAccessException(String.format("Was not able to access bucket %s", getBlobId(id)), e);
			}
		}
	}

	@Override
	public List<String> getContextList() {
		List<String> contextList = new ArrayList<>();
		try {
			for (Blob contextBlob : storage.list(gcsProperties.getBucketName()).iterateAll()) {
				contextList.add(contextBlob.getName());
			}
			return contextList;
		} catch (StorageException e) {
			throw new GCSAccessException(String.format("Was not able to access bucket %s", gcsProperties.getBucketName()), e);
		}
	}

	private BlobId getBlobId(String id) {
		return BlobId.of(gcsProperties.getBucketName(), id);
	}


}
