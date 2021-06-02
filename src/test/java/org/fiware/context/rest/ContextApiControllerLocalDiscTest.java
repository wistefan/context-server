package org.fiware.context.rest;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.apache.commons.io.FileUtils;
import org.fiware.context.api.ContextServerApiTestClient;
import org.fiware.context.configuration.LocalDiscProperties;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@MicronautTest(environments = "test-local")
public class ContextApiControllerLocalDiscTest extends AbstractContextApiControllerTest {

	@Inject
	private LocalDiscProperties localDiscProperties;

	@Inject
	private ContextServerApiTestClient testClient;

	@Override
	protected void initiateContextMap() throws Exception {
		OBJECT_MAPPER.writeValue(new File(localDiscProperties.getContextFolder() + "/core-context.json"), getCoreContextObject());
		OBJECT_MAPPER.writeValue(new File(localDiscProperties.getContextFolder() + "/data-models.json"), getDataModelsObject());
	}

	@Override
	protected Map<String, Object> getPersistedContexts() throws IOException {
		Map<String, Object> contextMap = new HashMap<>();
		List<Path> pathList = Files.list(Path.of(localDiscProperties.getContextFolder())).collect(Collectors.toList());
		for (Path path : pathList) {
			contextMap.put(path.getFileName().toString(), OBJECT_MAPPER.readValue(Files.readString(path), Object.class));
		}
		return contextMap;
	}

	@Override
	protected void before() throws IOException {
		Files.createDirectory(Path.of(localDiscProperties.getContextFolder()));
	}

	@Override
	protected void cleanup() throws IOException {
		FileUtils.deleteDirectory(new File(localDiscProperties.getContextFolder()));
	}

	@Override
	protected ContextServerApiTestClient getTestClient() {
		return testClient;
	}
}
