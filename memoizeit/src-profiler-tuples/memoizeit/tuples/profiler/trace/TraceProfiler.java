package memoizeit.tuples.profiler.trace;


import java.io.IOException;

import memoizeit.asm.Debug;
import memoizeit.asm.Files;
import memoizeit.asm.profiler.raw.AbstractRawFileWriter;
import memoizeit.tuples.instr.Options;
import memoizeit.tuples.serialization.DepthLogger;
import memoizeit.tuples.serialization.Serializer;
import memoizeit.tuples.serialization.SerializerConstants;

public class TraceProfiler extends AbstractRawFileWriter {
		
	private final Serializer dumpResults;
	private final Serializer dumpParameters;
	private final Serializer dumpTargets;
	
	public TraceProfiler(final String filePath) {
		super(filePath, Files.TUPLES_TRACE);
		dumpResults = new Serializer(filePath, Files.TUPLES_RESULTS);
		dumpParameters = new Serializer(filePath, Files.TUPLES_PARAMETERS);
		dumpTargets = new Serializer(filePath, Files.TUPLES_TARGETS);
	}
	
	public void onMethodEntry(int index, long callIndex, final Object target) {
		writeHeader(TraceConstants.ON_ENTRY, index, callIndex);
		writeTarget(target);
	}
	
	public void onMethodExit(int index, long callIndex, final Object target) {
		writeHeader(TraceConstants.ON_EXIT, index, callIndex);
		writeTarget(target);
	}
		
	public void onMethodExitException(int index, long callIndex, final Object target) {
		writeHeader(TraceConstants.ON_EXIT_EXCEPTION, index, callIndex);
		writeTarget(target);
	}
			
	public void onArguments(int index, long callIndex, int numberOfArguments) {
		writeHeader(TraceConstants.ON_ARGUMENTS, index, callIndex);
		try {
			getStream().writeInt(numberOfArguments);
		} catch (final IOException ex) {
			ex.printStackTrace();
		}
	}
		
	public void onReturnValue(int index, long callIndex) {
		writeHeader(TraceConstants.ON_RETURN_VALUE, index, callIndex);		
	}
			
	private Serializer getForType(byte type) {
		if (type == SerializerConstants.TYPE_PARAMETER) {
			return dumpParameters;
		} else if (type == SerializerConstants.TYPE_RETURN) {
			return dumpResults;
		} else if (type == SerializerConstants.TYPE_TARGET) {
			return dumpTargets;
		}
		Debug.getInstance().debug("MemoizeIt.TraceProfiler", "getForType - This should not happen.");
		return null;
	}
	
	public void onDumpObject(byte type, int arg, int index, long call, final Object object) {
		if (object == null) {
			getForType(type).dumpNull(type, index, call, arg);
		} else {
			if (Options.useMaxDepth() && Options.logDepth()) {
				DepthLogger.getInstance().onDump(type, index, call, arg, object);			
			}
			//
			getForType(type).dumpObject(type, index, call, arg, object);
			//
			if (Options.useMaxDepth() && Options.logDepth()) {
				DepthLogger.getInstance().onDumpDone();		
			}
		}
		
	}
	
	public void onDumpTarget(byte type, int arg, int index, long call, final Object object) {
		if (object == null) {
			getForType(type).dumpNull(type, index, call, arg);
		} else {
			if (Options.useMaxDepth() && Options.logDepth()) {
				DepthLogger.getInstance().onDump(type, index, call, arg, object);			
			}
			//
			getForType(type).dumpTarget(type, index, call, arg, object);
			//
			if (Options.useMaxDepth() && Options.logDepth()) {
				DepthLogger.getInstance().onDumpDone();		
			}
		}
	}
	
	public void writeHeader(byte type, int index, long callIndex) {
		try {
			getStream().write(type);
			getStream().writeInt(index);
			getStream().writeLong(callIndex);
		} catch (final IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public void writeTarget(final Object target) {
		try {
			getStream().writeInt(System.identityHashCode(target));
			if (target != null) {				
				getStream().writeUTF(target.getClass().getName());
			}
		} catch (final IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public void writeObject(final Object object) {
		try {
			getStream().write(TraceConstants.TYPE_OBJECT);
			getStream().writeInt(System.identityHashCode(object));
		} catch (final IOException ex) {
			ex.printStackTrace();
		}
	}
				
	public void writeLong(long value) {
		try {
			getStream().write(TraceConstants.TYPE_LONG);
			getStream().writeLong(value);
		} catch (final IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public void writeDouble(double value) {
		try {
			getStream().write(TraceConstants.TYPE_DOUBLE);
			getStream().writeDouble(value);
		} catch (final IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public void writeVoid() {
		try {
			getStream().write(TraceConstants.TYPE_VOID);
		} catch (final IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public void writeException() {
		try {
			getStream().write(TraceConstants.TYPE_EXCEPTION);
		} catch (final IOException ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
		if (Options.useMaxDepth() && Options.logDepth()) {
			DepthLogger.getInstance().onStart();		
		}
		dumpResults.onStart();
		dumpParameters.onStart();
		dumpTargets.onStart();
	}
	
	@Override
	public void onShutdown() {
		if (Options.useMaxDepth() && Options.logDepth()) {
			DepthLogger.getInstance().onShutdown();		
		}
		dumpResults.onShutdown();
		dumpParameters.onShutdown();
		dumpTargets.onShutdown();
		super.onShutdown();
	}
	
}
