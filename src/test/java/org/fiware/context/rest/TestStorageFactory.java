package org.fiware.context.rest;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import org.fiware.context.storage.GCSStorageFactory;

@Factory
@Requires(property = "gcs.enabled", value = "true")
public class TestStorageFactory {

	@Bean
	@Replaces(bean = Storage.class)
	public Storage getLocalStorage(@Property(name = "gcs.host") String host, @Property(name = "gcs.port") String port) {
		return StorageOptions.newBuilder()
				.setHost(String.format("http://%s:%s", host, port))
				.build()
				.getService();
	}
}
