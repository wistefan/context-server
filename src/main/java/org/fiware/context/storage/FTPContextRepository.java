package org.fiware.context.storage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.Requires;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.fiware.context.configuration.FTPProperties;
import org.fiware.context.exception.CouldNotCreateContextException;
import org.fiware.context.exception.CouldNotDeleteException;
import org.fiware.context.exception.FTPServerNotAvailableException;
import org.fiware.context.exception.FileNotReadableException;
import org.fiware.context.exception.FolderNotReadableException;
import org.fiware.context.exception.NoSuchContextException;
import org.fiware.context.exception.RepositoryCreationException;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
// context scoped, to enable startup connectivity check
@Context
@Requires(property = "ftp.enabled", value = "true")
@RequiredArgsConstructor
public class FTPContextRepository implements ContextRepository {

	private static final String FILE_PATH_TEMPLATE = "%s/%s";
	private final FTPProperties ftpProperties;
	private final ObjectMapper objectMapper;
	private FTPClient ftpClient;

	@PostConstruct
	public void init() throws RepositoryCreationException {
		try {
			ftpClient = connectToFTPServer();
		} catch (Exception e) {
			throw new RepositoryCreationException(String.format("Was not able to initialize FTP repository with config: %s", ftpProperties), e);
		}
		log.info("FTP repository successfully started.");
	}

	@Override
	public Optional<String> createContext(Object ldContext) {
		String contextId = UUID.randomUUID().toString();
		return persistContextById(contextId, ldContext);
	}

	@Override
	public Optional<String> createContextWithId(String id, Object ldContext) {
		return persistContextById(id, ldContext);
	}

	@Override
	public void deleteContext(String id) {
		if (getContext(id).isEmpty()) {
			throw new NoSuchContextException(String.format("No context with id %s does exist.", id));
		}
		try {
			if (!ftpClient.deleteFile(getFilePath(id))) {
				throw new CouldNotDeleteException(String.format("Was not able to delete context %s on ftp.", id));
			}
		} catch (IOException e) {
			throw new CouldNotDeleteException(String.format("Was not able to delete context %s on ftp.", id));
		}
	}

	@Override
	public Optional<Object> getContext(String id) {
		if (!ftpClient.isConnected()) {
			ftpClient = connectToFTPServer();
		}
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			ftpClient.retrieveFile(getFilePath(id), outputStream);
			return Optional.of(objectMapper.readValue(outputStream.toByteArray(), Object.class));
		} catch (IOException e) {
			throw new FileNotReadableException(String.format("Was not able to read context %s from ftp: %s.", id, ftpProperties), e);
		}
	}

	@Override
	public List<String> getContextList() {
		if (!ftpClient.isConnected()) {
			ftpClient = connectToFTPServer();
		}
		try {
			return Arrays.stream(ftpClient.listFiles(ftpProperties.getContextFolder())).map(FTPFile::getName).collect(Collectors.toList());
		} catch (IOException e) {
			throw new FolderNotReadableException(String.format("Was not able to read the remote folder %s.", ftpProperties.getContextFolder()), e);
		}
	}

	private Optional<String> persistContextById(String contextId, Object ldContext) {
		if (!ftpClient.isConnected()) {
			ftpClient = connectToFTPServer();
		}
		try {
			InputStream inputStream = new ByteArrayInputStream(objectMapper.writeValueAsBytes(ldContext));
			ftpClient.storeFile(getFilePath(contextId), inputStream);
			return Optional.of(contextId);
		} catch (IOException e) {
			throw new CouldNotCreateContextException("Was not able to persist the context to ftp.", e);
		}
	}

	private String getFilePath(String contextId) {
		return String.format(FILE_PATH_TEMPLATE, ftpProperties.getContextFolder(), contextId);
	}

	private FTPClient connectToFTPServer() {
		FTPClient ftpClient = new FTPClient();
		try {
			ftpClient.connect(ftpProperties.getHostname(), ftpProperties.getPort());

			if (ftpProperties.isSecured()) {
				ftpClient.login(ftpProperties.getUsername(), ftpProperties.getPassword());
			}
			// if no exception is thrown, everything should be fine
			ftpClient.getStatus(ftpProperties.getContextFolder());
			return ftpClient;
		} catch (IOException e) {
			throw new FTPServerNotAvailableException(String.format("Was not able to connect to ftp with configuration: %s", ftpProperties), e);
		}
	}
}
