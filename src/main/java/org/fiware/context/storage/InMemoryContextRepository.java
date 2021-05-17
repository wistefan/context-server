package org.fiware.context.storage;

import io.micronaut.context.annotation.Requires;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fiware.context.exception.NoSuchContextException;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Singleton
@Requires(property = "memory.enabled", value = "true")
@RequiredArgsConstructor
public class InMemoryContextRepository implements ContextRepository {

	private final Map<String, Object> contextMap = new HashMap<>();

	@Override
	public Optional<String> createContext(Object ldContext) {
		String contextId = UUID.randomUUID().toString();
		contextMap.put(contextId, ldContext);
		return Optional.of(contextId);
	}

	@Override
	public Optional<String> createContextWithId(String id, Object ldContext) {
		if (contextMap.containsKey(id)) {
			log.warn("Context with id {} already exists.", id);
			return Optional.empty();
		}
		contextMap.put(id, ldContext);
		return Optional.of(id);
	}

	@Override
	public void deleteContext(String id) {
		if (contextMap.containsKey(id)) {
			contextMap.remove(id);
		} else {
			throw new NoSuchContextException(String.format("No context with id %s exists.", id));
		}
	}

	@Override
	public Optional<Object> getContext(String id) {
		return Optional.ofNullable(contextMap.get(id));
	}

	@Override
	public List<String> getContextList() {
		return new ArrayList<>(contextMap.keySet());
	}
}
