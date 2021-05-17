package org.fiware.context.storage;

import javax.inject.Singleton;
import java.util.HashMap;

/**
 * Hashmap to be used as an in memory context cache.
 */
@Singleton
public class ContextMap extends HashMap<String, Object> {
}
