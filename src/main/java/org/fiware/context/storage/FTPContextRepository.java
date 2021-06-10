package org.fiware.context.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.Requires;
import jdk.jfr.Experimental;
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
import org.fiware.context.exception.ContextAlreadyExistsException;

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

/**
 * Repository implementation using an ftp-server as a backend.
 * Currently considered to be experimental.
 */
@Experimental
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

	/**
	 * Check if the ftp is available on startup
	 *
	 * @throws RepositoryCreationException - exception to be thrown in case the ftp was not available and therefore the repository cannot be created.
	 */
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
			if (!ftpClient.deleteFile(id)) {
				throw new CouldNotDeleteException(String.format("Was not able to delete context %s on ftp.", id), id);
			}
		} catch (IOException e) {
			throw new CouldNotDeleteException(String.format("Was not able to delete context %s on ftp.", id), id);
		}
	}

	@Override
	public Optional<Object> getContext(String id) {
		if (!ftpClient.isConnected()) {
			ftpClient = connectToFTPServer();
		}
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			ftpClient.retrieveFile(id, outputStream);
			return Optional.of(objectMapper.readValue(outputStream.toByteArray(), Object.class));
		} catch (MismatchedInputException e) {
			return Optional.empty();
		} catch (IOException e) {
			throw new FileNotReadableException(String.format("Was not able to read context %s from ftp: %s.", id, ftpProperties), e, id);
		}
	}

	@Override
	public List<String> getContextList() {
		if (!ftpClient.isConnected()) {
			ftpClient = connectToFTPServer();
		}
		try {
			return Arrays.stream(ftpClient.listFiles()).map(FTPFile::getName).collect(Collectors.toList());
		} catch (IOException e) {
			throw new FolderNotReadableException(String.format("Was not able to read the remote folder %s.", ftpProperties.getContextFolder()), e);
		}
	}

	/**
	 * Persist the context object with the given id.
	 *
	 * @param contextId - id to persist the context at.
	 * @param ldContext -  the context to be persisted
	 * @return the id of the context, in case it was created.
	 */
	private Optional<String> persistContextById(String contextId, Object ldContext) {
		if (getContext(contextId).isPresent()) {
			throw new ContextAlreadyExistsException(String.format("The context %s already exists.", contextId), contextId);
		}
		if (!ftpClient.isConnected()) {
			ftpClient = connectToFTPServer();
		}
		try {
			InputStream inputStream = new ByteArrayInputStream(objectMapper.writeValueAsBytes(ldContext));
			ftpClient.storeFile(contextId, inputStream);
			return Optional.of(contextId);
		} catch (IOException e) {
			throw new CouldNotCreateContextException("Was not able to persist the context to ftp.", e);
		}
	}

	/**
	 * Connect to the ftp and return a client.
	 *
	 * @return the connected client
	 */
	private FTPClient connectToFTPServer() {
		FTPClient client = new FTPClient();
		try {
			client.connect(ftpProperties.getHostname(), ftpProperties.getPort());

			if (ftpProperties.isSecured()) {
				client.login(ftpProperties.getUsername(), ftpProperties.getPassword());
			}
			if (client.getStatus(ftpProperties.getContextFolder()) == null) {
				throw new FTPServerNotAvailableException(String.format("Was not able to connect to ftp with configuration: %s", ftpProperties));
			}
			client.changeWorkingDirectory(ftpProperties.getContextFolder());
			return client;
		} catch (IOException e) {
			throw new FTPServerNotAvailableException(String.format("Was not able to connect to ftp with configuration: %s", ftpProperties), e);
		}
	}
}
