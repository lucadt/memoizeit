package memoizeit.analysis.version;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import memoizeit.tuples.serialization.Tags;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public final class Version {
	
	public static final class Repository {
		
		private static int GLOBAL_CURRENT_CLASS_VERSION;
		private static final Map<String, Integer> CLASS_VERSIONS;
		private static final Map<Integer, Integer> CLASS_HASH_VERSIONS;

		static {
			GLOBAL_CURRENT_CLASS_VERSION = INITIAL_MAJOR_VERSION;
			CLASS_VERSIONS = new ConcurrentHashMap<String, Integer>();
			CLASS_HASH_VERSIONS = new ConcurrentHashMap<Integer, Integer>();
		}
		
		public static final int getOrCreateClassVersion(final String className) {
			if (!CLASS_VERSIONS.containsKey(className)) {
				CLASS_VERSIONS.put(className, ++GLOBAL_CURRENT_CLASS_VERSION);
			}
			return CLASS_VERSIONS.get(className);
		}
		
		private static final int getOrCreateHashVersion(int classVersion) {
			if (!CLASS_HASH_VERSIONS.containsKey(classVersion)) {
				CLASS_HASH_VERSIONS.put(classVersion, INITIAL_MAJOR_VERSION);				
			}
			return CLASS_HASH_VERSIONS.get(classVersion);
		}
		
		public static final int getNewHashVersion(int classVersion) {
			int hashVersion = getOrCreateHashVersion(classVersion);
			hashVersion = hashVersion + 1;
			CLASS_HASH_VERSIONS.put(classVersion, hashVersion);
			return hashVersion;
		}
		
	}
		
	public static final int NULL_MAJOR_VERSION = Integer.MIN_VALUE;
	public static final int INITIAL_MAJOR_VERSION = 0;

	private static final Table<Integer, Integer, Version> CACHE = HashBasedTable.create();
	protected static final Table<Integer, String, Integer> VERSIONS = HashBasedTable.create();

	public static final Version NULL_VERSION = getOrCreate(NULL_MAJOR_VERSION, NULL_MAJOR_VERSION);
	public static final Version INVALID_VERSION = getOrCreate(INITIAL_MAJOR_VERSION, INITIAL_MAJOR_VERSION);
		
	public static synchronized int getOrCreateMinor(int classVersion, final String hash) {
		if (VERSIONS.contains(classVersion, hash)) {
			return VERSIONS.get(classVersion, hash);
		}
		int hashVersion = Repository.getNewHashVersion(classVersion);
		VERSIONS.put(classVersion, hash, hashVersion);
		return hashVersion;
	}
	
	public static synchronized Version getOrCreateInitial(final String className) {
		int classVersion = Repository.getOrCreateClassVersion(className);
		int hashVersion = INITIAL_MAJOR_VERSION;
		if (VERSIONS.contains(classVersion, className)) {
			hashVersion = VERSIONS.get(classVersion, className);
		} else {
			hashVersion = Repository.getOrCreateHashVersion(classVersion);
			VERSIONS.put(classVersion, className, hashVersion);
		}
		return Version.getOrCreate(classVersion, hashVersion);
	}
	
	public static synchronized Version getOrCreate(int classVersion, int hashVersion) {
		if (CACHE.contains(classVersion, hashVersion)) {
			return CACHE.get(classVersion, hashVersion);
		}		
		final Version version = new Version();
		version.major = classVersion;
		version.minor = hashVersion;
		CACHE.put(classVersion, hashVersion, version);
		return version;
	}
	
	private int minor;
	private int major;
	
	public boolean isNull() {
		return major == NULL_MAJOR_VERSION;
	}
	
	public boolean isInvalid() {
		return major == INITIAL_MAJOR_VERSION;
	}
	
	public boolean isType() {
		return !isNull() && !isInvalid() && minor == INITIAL_MAJOR_VERSION;
	}

	public int getMinor() {
		return minor;
	}

	public int getMajor() {
		return major;
	}

	@Override
	public String toString() {
		if (isNull()) {
			return Tags.NULL;
		}  else if (isInvalid()){
			return Tags.INVALID;
		} else {
			return String.format("@v%d_%d", major, minor);						
		}
	}
	
}