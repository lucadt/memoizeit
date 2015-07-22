package memoizeit.tuples.profiler;

import memoizeit.asm.util.DumpHelper;
import memoizeit.tuples.instr.Options;
import memoizeit.tuples.profiler.trace.TraceProfiler;

public final class Profiler {
		
	private static final ThreadLocal<Boolean> inProfiler = new ThreadLocal<Boolean>() {
		protected Boolean initialValue() {
			return false;
		};
	};

	private static final ThreadLocal<TraceProfiler> myProfiler = new ThreadLocal<TraceProfiler>() {
		protected TraceProfiler initialValue() {
			final Thread thread = Thread.currentThread();
			final String threadFileName = DumpHelper.getThreadIdentifier(thread.getId(), thread.getName());
			final String threadFilePath = Options.getDataDirectory() + '/' + threadFileName;
			final TraceProfiler profiler = new TraceProfiler(threadFilePath);
			MyManager.getInstance().register(thread, profiler);
			return profiler;
		};
	};
		
	public static void onMethodEntry(int index, long call, final Object target) {
		if (inProfiler.get()) {
			return;
		}
		inProfiler.set(true);
		myProfiler.get().onMethodEntry(index, call, target);
		inProfiler.set(false);
	}
	
	public static void onArguments(int index, long call, int numberOfArguments) {
		if (inProfiler.get()) {
			return;
		}
		inProfiler.set(true);
		myProfiler.get().onArguments(index, call, numberOfArguments);
		inProfiler.set(false);
	}
	
	public static void onValue(long value) {
		if (inProfiler.get()) {
			return;
		}
		inProfiler.set(true);
		myProfiler.get().writeLong(value);
		inProfiler.set(false);
	}
	
	public static void onValue(double value) {
		if (inProfiler.get()) {
			return;
		}
		inProfiler.set(true);
		myProfiler.get().writeDouble(value);
		inProfiler.set(false);
	}
	
	public static void onObject(final Object value) {
		if (inProfiler.get()) {
			return;
		}
		inProfiler.set(true);
		myProfiler.get().writeObject(value);
		inProfiler.set(false);
	}
	
	public static void onVoid() {
		if (inProfiler.get()) {
			return;
		}
		inProfiler.set(true);
		myProfiler.get().writeVoid();
		inProfiler.set(false);
	}
	
	public static void onException() {
		if (inProfiler.get()) {
			return;
		}
		inProfiler.set(true);
		myProfiler.get().writeException();
		inProfiler.set(false);
	}
	
	public static void onMethodExit(int index, long call, final Object target) {
		if (inProfiler.get()) {
			return;
		}
		inProfiler.set(true);
		myProfiler.get().onMethodExit(index, call, target);
		inProfiler.set(false);
	}
	
	public static void onMethodException(int index, long call, final Object target) {
		if (inProfiler.get()) {
			return;
		}
		inProfiler.set(true);
		myProfiler.get().onMethodExitException(index, call, target);
		inProfiler.set(false);
	}
	
	public static void onReturnValue(int index, long call) {
		if (inProfiler.get()) {
			return;
		}
		inProfiler.set(true);
		myProfiler.get().onReturnValue(index, call);
		inProfiler.set(false);
	}
	
	public static void onDumpTarget(final Object object, byte type, int arg, int index, long call) {
		if (inProfiler.get()) {
			return;
		}
		inProfiler.set(true);
		myProfiler.get().onDumpTarget(type, arg, index, call, object);
		inProfiler.set(false);
	}
	
	public static void onDumpObject(final Object object, byte type, int arg, int index, long call) {
		if (inProfiler.get()) {
			return;
		}
		inProfiler.set(true);
		myProfiler.get().onDumpObject(type, arg, index, call, object);
		inProfiler.set(false);
	}
				
}
