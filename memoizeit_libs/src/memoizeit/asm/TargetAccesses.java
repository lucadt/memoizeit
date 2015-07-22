package memoizeit.asm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import memoizeit.asm.util.DumpHelper;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public final class TargetAccesses implements java.io.Serializable {
	
	private static final long serialVersionUID = 8468193695146032872L;
	
	private static final ThreadLocal<TargetAccesses> INSTANCE = new ThreadLocal<TargetAccesses>() {
		protected TargetAccesses initialValue() {
			final TargetAccesses ta = (TargetAccesses) DumpHelper.readObjectFromFile(BaseOptions.getFieldsOutputFile());
			if (ta == null) {
				Debug.getInstance().debug("TargetAccesses", "Field profiling data is *not* loaded");
				final TargetAccesses emptyAccesses = new TargetAccesses();
				emptyAccesses.setLoaded(false);
				return emptyAccesses;
			}
			Debug.getInstance().debug("TargetAccesses", "Field profiling data is loaded");
			ta.setLoaded(true);
			return ta;
		};
	};
	
	public static TargetAccesses getInstance() {
		return INSTANCE.get();
    }
	
	private transient boolean loaded;
	
	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}

	public boolean isLoaded() {
		return loaded;
	}
	
	public static final class Accesses implements java.io.Serializable {

		private static final long serialVersionUID = 937651921607208937L;
		
		private final int identifier;
		private final String type;
		private final Map<String, Set<String>> fields;

		private boolean adjusted;
		
		public static final Accesses create(int identifier, final String type) {
			return new Accesses(identifier, type);
		}

		private Accesses(int identifier, final String type) {
			this.identifier = identifier;
			this.type = type;
			this.fields = new HashMap<String, Set<String>>();
			this.adjusted = false;
		}

		public boolean isAdjusted() {
			return adjusted;
		}

		public void setAdjusted(boolean adjusted) {
			this.adjusted = adjusted;
		}

		public int getIdentifier() {
			return identifier;
		}

		public String getType() {
			return type;
		}

		public void add(final String name, final String field) {
			if (!fields.containsKey(name)) {
				fields.put(name, new HashSet<String>());
			}
			fields.get(name).add(field);
		}
		
		public boolean has(final String name) {
	    	return fields.containsKey(name);
		}

		public Set<String> get(final String name) {
	    	return fields.get(name);
		}
		
	}
	
	private Table<Integer, String, Accesses> models = HashBasedTable.create();
    
    public final Accesses get(int method, final String type) {
    	return models.get(method, type);
    }
    
	public final Accesses getOrCreate(int method, final String type) {
		if (!models.contains(method, type)) {
			models.put(method, type, Accesses.create(method, type));
		}
		return get(method, type);
	}

	public final boolean isAccessingFields(int method) {
		return models.containsRow(method);
	}
	
	public final boolean isAccessingFields(int method, final String type) {
		return models.contains(method, type);
	}
    
}
