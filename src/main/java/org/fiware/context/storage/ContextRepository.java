package org.fiware.context.storage;

import java.util.List;
import java.util.Optional;

/**
 * Repository for storing and retrieving contexts.
 */
public interface ContextRepository {

	/**
	 * Create the given context inside the repository.
	 *
	 * @param ldContext - object containing the context information
	 * @return the id of the context, if created
	 */
	Optional<String> createContext(Object ldContext);

	/**
	 * Create the given context inside the repository with the given id
	 *
	 * @param id        - id to be used for the context
	 * @param ldContext - object containing the context information
	 * @return the id of the context, if created
	 */
	Optional<String> createContextWithId(String id, Object ldContext);

	/**
	 * Delete the context with the given id.
	 *
	 * @param id - id of the context
	 */
	void deleteContext(String id);

	/**
	 * Get the context with the given id.
	 *
	 * @param id - id of the context
	 * @return the context object, if exists.
	 */
	Optional<Object> getContext(String id);

	/**
	 * List all context-ids available in the repository
	 *
	 * @return List of context-ids
	 */
	List<String> getContextList();
}
