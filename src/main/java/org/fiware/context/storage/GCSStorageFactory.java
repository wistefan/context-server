package org.fiware.context.storage;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;

/**
 * Factory to create the gcs-storage bean
 */
@Factory
public class GCSStorageFactory {

	/**
	 * Bean to be created for accessing the gcs-storage
	 *
	 * @return the storage bean
	 */
	@Bean
	public Storage getDefaultStorage() {
		return  StorageOptions.getDefaultInstance().getService();
	}

}
