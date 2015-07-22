package memoizeit.asm.util.ids;

import java.util.HashMap;
import java.util.Map;

public final class Mapping {
	
	private int currentId;
	private final Map<Integer, Integer> mappings = new HashMap<Integer, Integer>(1024);
	
	public int getNewIdentifier() {
		return currentId++;
	}
	
	public int getOrCreateMapping(final int identifier) {
		if (!mappings.containsKey(identifier)) {
			mappings.put(identifier, currentId++);
		}
		return mappings.get(identifier);
	}
	
	public int getMapping(final int identifer) {
		final Integer id = mappings.get(identifer);
		if (id != null) {
			return id;
		}
		throw new RuntimeException("Object: " + identifer + " has no identifier");	
	}
	
	public void reset() {
		currentId = 0;
		mappings.clear();
	}

}
