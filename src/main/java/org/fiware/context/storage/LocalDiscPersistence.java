package org.fiware.context.storage;

import javax.inject.Singleton;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Filesystem wrapper bean to allow replacement in unit tests.
 */
@Singleton
public class LocalDiscPersistence {

	public void deleteFile(Path path) throws IOException {
		Files.delete(path);
	}

	public boolean exists(Path path) {
		return Files.exists(path);
	}

	public String readString(Path path) throws IOException {
		return Files.readString(path);
	}

	public Stream<Path> list(Path path) throws IOException {
		return Files.list(path);
	}

}
