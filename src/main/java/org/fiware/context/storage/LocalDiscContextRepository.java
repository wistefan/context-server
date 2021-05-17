package org.fiware.context.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.context.annotation.Requires;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fiware.context.configuration.LocalDiscProperties;
import org.fiware.context.exception.CouldNotCreateContextException;
import org.fiware.context.exception.CouldNotDeleteException;
import org.fiware.context.exception.FileNotReadableException;
import org.fiware.context.exception.FolderNotReadableException;
import org.fiware.context.exception.NoSuchContextException;

import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Singleton
@Requires(property = "local.enabled", value = "true")
@RequiredArgsConstructor
public class LocalDiscContextRepository implements ContextRepository {

	private static final String CONTEXT_FILENAME_TEMPLATE = "%s/%s.json";

	private final LocalDiscProperties localDiscProperties;
	private final ObjectMapper objectMapper;

	@Override
	public Optional<String> createContext(Object ldContext) {
		String contextId = UUID.randomUUID().toString();
		return storeContextById(contextId, ldContext);
	}

	private Optional<String> storeContextById(String contextId, Object ldContext) {
		Path filePath = getFilePath(contextId);
		if (Files.exists(filePath)) {
			log.warn("Context with id {} already exists.", contextId);
			return Optional.empty();
		}
		try {
			objectMapper.writeValue(new File(filePath.toUri()), ldContext);
		} catch (IOException e) {
			throw new CouldNotCreateContextException("Was not able to create the context.", e);
		}
		return Optional.of(contextId);
	}

	@Override
	public Optional<String> createContextWithId(String id, Object ldContext) {

		return storeContextById(id, ldContext);
	}

	@Override
	public void deleteContext(String id) {
		Path filePath = getFilePath(id);
		if (Files.exists(filePath)) {
			try {
				Files.delete(filePath);
			} catch (IOException e) {
				throw new CouldNotDeleteException(String.format("Was not able to delete file for context %s", id), e);
			}
		} else {
			throw new NoSuchContextException(String.format("Context with id %s does not exist.", id));
		}
	}

	@Override
	public Optional<Object> getContext(String id) {
		try {
			String context = Files.readString(getFilePath(id));
			return Optional.of(objectMapper.readValue(context, Object.class));
		} catch (IOException e) {
			throw new FileNotReadableException(String.format("Was not able to read file for context %s", id), e);
		}
	}

	@Override
	public List<String> getContextList() {
		try {
			return Files.list(Path.of(localDiscProperties.getContextFolder()))
					.map(Path::getFileName)
					.map(Path::toString)
					.map(filename -> filename.replace(".json", ""))
					.collect(Collectors.toList());
		} catch (IOException e) {
			throw new FolderNotReadableException(String.format("Was not able to retrieve the context list from %s.", localDiscProperties.getContextFolder()), e);
		}
	}

	private Path getFilePath(String id) {
		return Path.of(String.format(CONTEXT_FILENAME_TEMPLATE, localDiscProperties.getContextFolder(), id));
	}
}
