package org.fiware.context.rest;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.fiware.context.api.ContextServerApiTestClient;
import org.fiware.context.storage.ContextMap;

import javax.inject.Inject;
import java.util.Map;

@MicronautTest(environments = "test-in-memory")
public class ContextApiControllerInMemoryTest extends AbstractContextApiControllerTest {
	
	@Inject
	private ContextServerApiTestClient testClient;

	@Inject
	private ContextMap contextMap;

	public void cleanup() {
		contextMap.clear();
	}


	@Override
	protected void initiateContextMap() throws Exception {
		contextMap.put("core-context.json", getCoreContextObject());
		contextMap.put("data-models.json", getDataModelsObject());
	}

	@Override
	protected Map<String, Object> getPersistedContexts() {
		return contextMap;
	}

	@Override
	protected void before() {
	}

	@Override
	protected ContextServerApiTestClient getTestClient() {
		return testClient;
	}
}
