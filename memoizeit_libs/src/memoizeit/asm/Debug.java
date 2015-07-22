package memoizeit.asm;

import memoizeit.asm.profiler.text.AbstractTextFileWriter;

public final class Debug {
	
	private static DebuggerTextFile INSTANCE = null;
	
	public static synchronized final DebuggerTextFile getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new DebuggerTextFile();
			INSTANCE.onStart();
		}
		return INSTANCE;
	}
	
	public static final class DebuggerTextFile extends AbstractTextFileWriter {
		public DebuggerTextFile() {
			super(BaseOptions.getBaseDirectory(), Files.DEBUG_FILE, true);
		}
		public void debug(final String prefix, final String message) {
			getOutput().printf("[%s] %s\n", prefix, message);
			getOutput().flush();
		}
	}
	
}
