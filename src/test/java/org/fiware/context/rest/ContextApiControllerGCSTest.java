package org.fiware.context.rest;


import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.env.PropertySource;
import io.micronaut.runtime.server.EmbeddedServer;
import org.fiware.context.api.ContextServerApiTestClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.GenericContainer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ContextApiControllerGCSTest extends AbstractContextApiControllerTest {

	private static final GenericContainer GCS_CONTAINER = new GenericContainer("fsouza/fake-gcs-server:v1.25.0")
			.withCommand("-scheme http")
			.withExposedPorts(4443);
	public static final String LOCAL_BUCKET = "local-bucket";

	private static EmbeddedServer embeddedServer;
	private static ApplicationContext applicationContext;
	private static ContextServerApiTestClient testClient;
	private static Storage storage;

	@BeforeAll
	public static void beforeAll() {

		GCS_CONTAINER.start();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// continue, should only give the container a little time to start
		}
		storage = StorageOptions.newBuilder()
				.setHost(String.format("http://%s:%s", GCS_CONTAINER.getHost(), GCS_CONTAINER.getMappedPort(4443)))
				.build()
				.getService();
		storage.create(BucketInfo.of(LOCAL_BUCKET));

		embeddedServer = ApplicationContext.run(EmbeddedServer.class, PropertySource.of(
				"test-gcs",
				Map.of(
						"gcs.bucketName", LOCAL_BUCKET,
						"gcs.enabled", true,
						"gcs.host", GCS_CONTAINER.getHost(),
						"gcs.port", GCS_CONTAINER.getMappedPort(4443)
				)));
		applicationContext = embeddedServer.getApplicationContext();
	}

	@BeforeEach
	public void setup() throws MalformedURLException {
		testClient = applicationContext.getBean(ContextServerApiTestClient.class);
	}

	@Override
	protected ContextServerApiTestClient getTestClient() {
		return testClient;
	}

	@Override
	protected void initiateContextMap() throws Exception {
		BlobInfo blobInfoCoreContext = BlobInfo.newBuilder(BlobId.of(LOCAL_BUCKET, "core-context.json")).setContentType("text/plain").build();
		BlobInfo blobInfoDataModels = BlobInfo.newBuilder(BlobId.of(LOCAL_BUCKET, "data-models.json")).setContentType("text/plain").build();
		storage.create(blobInfoCoreContext, OBJECT_MAPPER.writeValueAsBytes(getCoreContextObject()));
		storage.create(blobInfoDataModels, OBJECT_MAPPER.writeValueAsBytes(getDataModelsObject()));

	}

	@Override
	protected Map<String, Object> getPersistedContexts() throws IOException {
		Map<String, Object> persistedContexts = new HashMap<>();
		for (Blob contextBlob : storage.list(LOCAL_BUCKET).iterateAll()) {
			persistedContexts.put(contextBlob.getName(), OBJECT_MAPPER.readValue(new String(storage.readAllBytes(contextBlob.getBlobId()), UTF_8), Object.class));
		}
		return persistedContexts;
	}

	@Override
	protected void before() throws IOException {
	}

	@Override
	protected void cleanup() throws IOException {
		for (Blob contextBlob : storage.list(LOCAL_BUCKET).iterateAll()) {
			storage.delete(contextBlob.getBlobId());
		}
	}
}
