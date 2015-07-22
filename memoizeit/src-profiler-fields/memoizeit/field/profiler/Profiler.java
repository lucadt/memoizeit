package memoizeit.field.profiler;

import memoizeit.asm.util.DumpHelper;
import memoizeit.field.instr.Options;

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
		
	public static final void onFieldStaticRead(int index, int field) {
		if (inProfiler.get()) {
			return;
		}
		inProfiler.set(true);
		myProfiler.get().onFieldStaticRead(index, field);
		inProfiler.set(false);
	}

	public static final void onFieldRead(final Object target, int index, int field) {
		if (inProfiler.get()) {
			return;
		}
		inProfiler.set(true);
		myProfiler.get().onFieldRead(index, field, target);
		inProfiler.set(false);
	}
	
	public static final void onFieldStaticWrite(int index, int field) {
		if (inProfiler.get()) {
			return;
		}
		inProfiler.set(true);
		myProfiler.get().onFieldStaticWrite(index, field);
		inProfiler.set(false);
	}
	
	public static final void onFieldWrite(final Object target, int index, int field) {
		if (inProfiler.get()) {
			return;
		}
		inProfiler.set(true);
		myProfiler.get().onFieldWrite(index, field, target);
		inProfiler.set(false);
	}

	public static final void onMethodEntry(int index, final Object target) {
		if (inProfiler.get()) {
			return;
		}
		inProfiler.set(true);
		myProfiler.get().onEntry(index, target);
		inProfiler.set(false);
	}
	
	public static final void onMethodExit(int index, final Object target) {
		if (inProfiler.get()) {
			return;
		}
		inProfiler.set(true);
		myProfiler.get().onExit(index, target);
		inProfiler.set(false);
	}
	
	public static final void onMethodException(int index, final Object target) {
		if (inProfiler.get()) {
			return;
		}
		inProfiler.set(true);
		myProfiler.get().onException(index, target);
		inProfiler.set(false);
	}
			
}
