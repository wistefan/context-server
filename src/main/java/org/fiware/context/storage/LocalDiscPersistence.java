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

	/**
	 * Delete the file
	 *
	 * @param path - path of the file to be deleted.
	 */
	public void deleteFile(Path path) throws IOException {
		Files.delete(path);
	}

	/**
	 * Check if a file with that path exists
	 *
	 * @param path - path to check
	 * @return true if a file on the path exists
	 */
	public boolean exists(Path path) {
		return Files.exists(path);
	}

	/**
	 * Read a string from the file at the given path
	 *
	 * @param path - path to be read from
	 * @return the string contained inside the file
	 */
	public String readString(Path path) throws IOException {
		return Files.readString(path);
	}

	/**
	 * List all files/folders at the given path.
	 *
	 * @param path - path to list from
	 * @return a stream of paths
	 */
	public Stream<Path> list(Path path) throws IOException {
		return Files.list(path);
	}

}
