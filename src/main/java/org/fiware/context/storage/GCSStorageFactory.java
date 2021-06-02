package org.fiware.context.storage;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;

@Factory
public class GCSStorageFactory {

	@Bean
	public Storage getDefaultStorage() {
		return  StorageOptions.getDefaultInstance().getService();
	}

}
