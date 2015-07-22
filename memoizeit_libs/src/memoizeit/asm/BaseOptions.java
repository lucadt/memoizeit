package memoizeit.asm;

public final class BaseOptions {
	
	public static String getTuplesDirectory() {
		return getBaseDirectory() + '/' + Files.TUPLES_DIR;
	}
	
	public static String getFieldsDirectory() {
		return getBaseDirectory() + '/' + Files.FIELDS_DIR;
	}
	
	public static String getTimeDirectory() {
		return getBaseDirectory() + '/' + Files.TIME_DIR;
	}
			
	public static String getMaximumDepthFile() {
		return getTuplesDirectory() + '/' + Files.TUPLES_MAX_DEPTH;
	}
	
	public static String getFieldsOutputFile() {
		return getFieldsDirectory() + '/' + Files.FIELDS_OUTPUT;
	}
		
	public static String getWhiteListFile() {
		return getBaseDirectory() + '/' + Files.WHITE_LIST_FILE;
	}
	
	public static String getTotalTimeFile() {
		return getTimeDirectory() + '/' + Files.TIME_TOTAL;
	}
	
	public static String getOptionsFile() {
		if (BaseOptions.isOptionsFileSet()) {
			return BaseOptions.getOptionsFileValue();
		}
		return "options.json";
	}
	
	private static boolean isOptionsFileSet() {
		return getOptionsFileValue() != null;
	}
	
	private static String getOptionsFileValue() {
		return System.getProperty("analysis.project.options");
	}
	
	public static boolean useCompressedStream() {
		if (isCompressedStreamSet()) {
			final String useCompressedStrig = getCompressedStreamValue();
			if (useCompressedStrig != null && useCompressedStrig.equals("true")) {
				return true;
			}
		}	
		return false;
	}
	
	private static boolean isCompressedStreamSet() {
		return getCompressedStreamValue() != null;
	}
	
	private static String getCompressedStreamValue() {
		return System.getProperty("analysis.project.compressed");
	}
	
	public static String getBaseDirectory() {
		if (BaseOptions.isProjectFolderSet()) {
			return BaseOptions.getProjectFolder();
		}
		return ".";
	}
	
	private static final String getProjectFolder() {
		return System.getProperty("analysis.project.folder");
	}
	
	private static final boolean isProjectFolderSet() {
		return getProjectFolder() != null;
	}
	
	private static final String getProjectPrefix() {
		return System.getProperty("analysis.project.package");
	}
	
	private static final boolean isProjectPrefixSet() {
		return getProjectPrefix() != null;
	}

	public static final boolean isProjectClass(final String className) {
		if (isProjectPrefixSet()) {
			final String classNameNew = className.replace('/', '.');
			return classNameNew.startsWith(getProjectPrefix());
		}			
		return true;
	}
	
}
