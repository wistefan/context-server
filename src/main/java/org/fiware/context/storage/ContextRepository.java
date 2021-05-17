package org.fiware.context.storage;

import java.util.List;
import java.util.Optional;

public interface ContextRepository {

	Optional<String> createContext(Object ldContext);

	Optional<String> createContextWithId(String id, Object ldContext);

	void  deleteContext(String id);

	Optional<Object> getContext(String id);

	List<String> getContextList();
}
