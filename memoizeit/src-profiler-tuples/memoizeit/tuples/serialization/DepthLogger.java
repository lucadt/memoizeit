package memoizeit.tuples.serialization;

import memoizeit.asm.Files;
import memoizeit.asm.profiler.text.AbstractTextFileWriter;
import memoizeit.asm.util.DumpHelper;
import memoizeit.tuples.instr.Options;

public final class DepthLogger extends AbstractTextFileWriter {
	
	private static final ThreadLocal<DepthLogger> INSTANCE = new ThreadLocal<DepthLogger>() {
		protected DepthLogger initialValue() {
			final Thread thread = Thread.currentThread();
			final String threadFileName = DumpHelper.getThreadIdentifier(thread.getId(), thread.getName());
			final String threadFilePath = Options.getDataDirectory() + '/' + threadFileName;
			return new DepthLogger(threadFilePath);
		};
	};
	
	public static DepthLogger getInstance() {
		return INSTANCE.get();
	}
	
	private byte currentType;
	private long currentCall;
	private int currentIndex;
	private int currentArg;
	private int currentObject;
	private int currentDepth;

	private DepthLogger(final String path) {
		super(path, Files.TUPLES_LOG_DEPTH);
		onResetCurrent();
	}
	
	private void onResetCurrent() {
		currentIndex = Integer.MIN_VALUE;
		currentObject = Integer.MIN_VALUE;
		currentArg = Integer.MIN_VALUE;
		currentCall = Long.MIN_VALUE;
		currentDepth = Integer.MIN_VALUE;
		currentType = Byte.MIN_VALUE;
	}
	
	public void onDump(byte type, int index, long call, int arg, final Object anObject) {
		currentType = type;
		currentIndex = index;
		currentCall = call;
		currentArg = arg;
		currentObject = System.identityHashCode(anObject);
		currentDepth = Integer.MIN_VALUE;
	}
		
	public void onDepth(int depth) {
		currentDepth = Math.max(currentDepth, depth);
	}
	
	public void onDumpDone() {
		getOutput().printf("%d;%d;%d;%d;%d;%d\n", 
				currentType, 
				currentIndex, 
				currentCall, 
				currentArg, 
				currentObject, 
				currentDepth);
		getOutput().flush();
		onResetCurrent();
	}
		
}
