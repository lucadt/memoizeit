package memoizeit.analysis.version.trace;

import java.io.EOFException;
import java.io.IOException;

import memoizeit.asm.Debug;
import memoizeit.asm.profiler.raw.AbstractRawFileReader;
import memoizeit.tuples.profiler.trace.TraceConstants;

public final class Trace extends AbstractRawFileReader {
	
	private final ICallback callback;
	
	private int tempIndex = Integer.MIN_VALUE;
	private long tempCallIndex = Long.MIN_VALUE;
	private int tempTarget = Integer.MIN_VALUE;
	private String tempTargetType = null;

	public Trace(final String filePath, final String fileName, final ICallback callback) {
		super(filePath, fileName);
		this.callback = callback;
	}
		
	private void onResetTemps() {
		tempIndex = Integer.MIN_VALUE;
		tempCallIndex = Long.MIN_VALUE;
		tempTarget = Integer.MIN_VALUE;
		tempTargetType = null;
	}
	
	private void readHeader() throws IOException {
		tempIndex = getStream().readInt();
		tempCallIndex = getStream().readLong();
	}
	
	private void readTarget() throws IOException {
		tempTarget = getStream().readInt();
		if (tempTarget != 0) {
			tempTargetType = getStream().readUTF();
		}
	}
	
	public void onReadMethodEntry() throws IOException {
		readHeader();
		readTarget();
		callback.onEntry(tempIndex, tempCallIndex, tempTarget, tempTargetType);
	}
	
	public void onReadMethodExit() throws IOException {
		readHeader();
		readTarget();
		callback.onExit(tempIndex, tempCallIndex, tempTarget, tempTargetType);	
	}
	
	public void onMethodExitException() throws IOException {
		readHeader();
		readTarget();
		callback.onExitException(tempIndex, tempCallIndex, tempTarget, tempTargetType);
	}
	
	public void onArguments() throws IOException {
		readHeader();
		int numberOfArguments = getStream().readInt();
		callback.onArguments(tempIndex, tempCallIndex, numberOfArguments);
		for (int i=0; i < numberOfArguments; i++) {
			byte argType = getStream().readByte();
			switch (argType) {
			case TraceConstants.TYPE_LONG:
				callback.onArgument(i, getStream().readLong());
				break;
			case TraceConstants.TYPE_OBJECT:
				callback.onArgument(i, getStream().readInt());
				break;
			case TraceConstants.TYPE_DOUBLE:
				callback.onArgument(i, getStream().readDouble());
				break;
			}
		}
		callback.onArgumentsDone();
	}
	
	public void onReturnValue() throws IOException {
		readHeader();
		byte returnType = getStream().readByte();
		switch (returnType) {
		case TraceConstants.TYPE_LONG:
			callback.onReturnLong(tempIndex, tempCallIndex, getStream().readLong());
			break;
		case TraceConstants.TYPE_OBJECT:
			callback.onReturnObject(tempIndex, tempCallIndex, getStream().readInt());
			break;
		case TraceConstants.TYPE_DOUBLE:
			callback.onReturnDouble(tempIndex, tempCallIndex, getStream().readDouble());
			break;		
		case TraceConstants.TYPE_VOID:
			callback.onReturnVoid(tempIndex, tempCallIndex);
			break;
		case TraceConstants.TYPE_EXCEPTION:
			callback.onReturnException(tempIndex, tempCallIndex);
			break;
		}
	}
	
	public void read() {
		try {
			onStart();
			Debug.getInstance().debug("Version.Trace", "Start");
			while (true) {
				onResetTemps();
				byte type = getStream().readByte();
				switch (type) {
				case TraceConstants.ON_ENTRY:
					onReadMethodEntry();
					break;
				case TraceConstants.ON_EXIT:
					onReadMethodExit();
					break;
				case TraceConstants.ON_EXIT_EXCEPTION:
					onMethodExitException();
					break;
				case TraceConstants.ON_ARGUMENTS:
					onArguments();
					break;
				case TraceConstants.ON_RETURN_VALUE:
					onReturnValue();
					break;	
				}
			}
		} catch (final EOFException ex) {
			onShutdown();
			Debug.getInstance().debug("Version.Trace", "End");
		} catch (final IOException ex) {
			ex.printStackTrace();
		}
	}
		
}
