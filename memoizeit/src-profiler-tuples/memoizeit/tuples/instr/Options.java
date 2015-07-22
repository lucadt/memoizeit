package memoizeit.tuples.instr;

import memoizeit.asm.BaseOptions;
import memoizeit.asm.Files;

public final class Options {
	
	public static String getDataDirectory() {
		return BaseOptions.getTuplesDirectory() + '/' + Files.DATA_DIR;
	}
	
	public static String getBytecodeDirectory() {
		return BaseOptions.getTuplesDirectory() + '/' + Files.BYTECODE_DIR;
	}
	
	public static String getMethodsFile() {
		return BaseOptions.getTuplesDirectory() + '/' + Files.METHODS_FILE;
	}
			
	private static boolean ON_DUMP_BYTECODE = false;
	private static boolean ON_USE_CUSTOM_HASHCODE = false;
	private static boolean ON_LOG_DEPTH = false;

	private static boolean ON_USE_MAX_DEPTH = false;
	private static int MAX_DEPTH = Integer.MIN_VALUE;
	
	private static boolean ON_USE_EARLY_STOP_PROFILING = false;

	static {
		onReadDumpBytecode();
		onReadUseCustomHashCode();
		onReadUseMaxDepth();
		onReadMaxDepth();
		onReadLogDepth();
		onReadUseEarlyStopProfiling();
	}
		
	public static void onReadDumpBytecode() {
		final String dumpBytecodeString = System.getProperty("analysis.dump.bytecode");
		if (dumpBytecodeString != null && dumpBytecodeString.equals("true")) {
			ON_DUMP_BYTECODE = true;
		} else {
			ON_DUMP_BYTECODE = false;
		}
	}
	
	public static void onReadUseCustomHashCode() {
		final String useCustomHashCode = System.getProperty("analysis.dump.hashcode");
		if (useCustomHashCode != null && useCustomHashCode.equals("true")) {
			ON_USE_CUSTOM_HASHCODE = true;
		} else {
			ON_USE_CUSTOM_HASHCODE = false;
		}
	}
	
	public static void onReadUseMaxDepth() {
		final String usedDepthString = System.getProperty("analysis.depth.use");
		if (usedDepthString != null && usedDepthString.equals("true")) {
			ON_USE_MAX_DEPTH = true;
		} else {
			ON_USE_MAX_DEPTH = false;
		}
	}
	
	public static void onReadMaxDepth() {
		final String maxDepthString = System.getProperty("analysis.depth.max");
		if (maxDepthString != null) {
			MAX_DEPTH = Integer.parseInt(maxDepthString);
		} else {
			MAX_DEPTH = Integer.MIN_VALUE;
		}
	}
	
	public static void onReadLogDepth() {
		final String logDepthString = System.getProperty("analysis.depth.log");
		if (logDepthString != null && logDepthString.equals("true")) {
			ON_LOG_DEPTH = true;
		} else {
			ON_LOG_DEPTH = false;
		}
	}
	
	public static void onReadUseEarlyStopProfiling() {
		final String useCustomHashCode = System.getProperty("analysis.profiling.earlystop");
		if (useCustomHashCode != null && useCustomHashCode.equals("true")) {
			ON_USE_EARLY_STOP_PROFILING = true;
		} else {
			ON_USE_EARLY_STOP_PROFILING = false;
		}
	}
		
	public static boolean dumpBytecode() {
		return ON_DUMP_BYTECODE;
	}
	
	public static boolean useCustomHashCode() {
		return ON_USE_CUSTOM_HASHCODE;
	}
	
	public static boolean useMaxDepth() {
		return ON_USE_MAX_DEPTH;
	}
	
	public static boolean logDepth() {
		return ON_LOG_DEPTH;
	}
	
	public static int getMaxDepth() {
		return MAX_DEPTH;
	}
	
	public static boolean useEarlyStopProfiling() {
		return ON_USE_EARLY_STOP_PROFILING;
	}
	
}
