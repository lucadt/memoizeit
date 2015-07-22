package memoizeit.analysis.fields;

import java.io.EOFException;
import java.io.IOException;

import memoizeit.asm.Debug;
import memoizeit.asm.profiler.raw.AbstractRawPipedFileReader;
import memoizeit.field.profiler.TraceProfiler;

public final class Trace extends AbstractRawPipedFileReader {
	
	private ICallback callback;
	
	public Trace(final String filePath, final String fileName, final ICallback callback) {
		super(filePath, fileName);
		this.callback = callback;
	}
	
	private void readEntry() throws IOException {
		int index = getStream().readInt();
		int target = getStream().readInt();
		if (target != 0) {
			final String targetTypeString = getStream().readUTF();
			callback.onEntry(index, target, targetTypeString);
		} else {
			callback.onEntry(index, target, null);
		}
	}
	
	private void readExit() throws IOException {
		int index = getStream().readInt();
		int target = getStream().readInt();
		callback.onExit(index, target);
	}
	
	private void readExitException() throws IOException {
		int index = getStream().readInt();
		int target = getStream().readInt();
		callback.onExitException(index, target);
	}
	
	private void readFieldRead() throws IOException {
		int index = getStream().readInt();
		int field = getStream().readInt();
		int target = getStream().readInt();
		callback.onFieldRead(index, field, target);
	}

	private void readFieldWrite() throws IOException {
		int index = getStream().readInt();
		int field = getStream().readInt();
		int target = getStream().readInt();
		callback.onFieldWrite(index, field, target);
	}
	
	private void readFieldStaticRead() throws IOException {
		int index = getStream().readInt();
		int field = getStream().readInt();
		callback.onFieldStaticRead(index, field);
	}
	
	private void readFieldStaticWrite() throws IOException {
		int index = getStream().readInt();
		int field = getStream().readInt();
		callback.onFieldStaticWrite(index, field);
	}
	
	public void read() {
		try {
			onStart();
			Debug.getInstance().debug("Fields.Trace", "Start");
			while (true) {
				byte type = getStream().readByte();
				switch (type) {
				case TraceProfiler.ON_ENTRY:
					readEntry();
					break;
				case TraceProfiler.ON_EXIT:
					readExit();
					break;
				case TraceProfiler.ON_EXCEPTION:
					readExitException();
					break;
				case TraceProfiler.ON_FIELD_READ:
					readFieldRead();
					break;
				case TraceProfiler.ON_FIELD_WRITE:
					readFieldWrite();
					break;
				case TraceProfiler.ON_STATIC_FIELD_READ:
					readFieldStaticRead();
					break;
				case TraceProfiler.ON_STATIC_FIELD_WRITE:
					readFieldStaticWrite();
					break;
				default:
					Debug.getInstance().debug("Fields.Trace", "Wrong type: " + type);
					break;
				}
			}
		} catch (final EOFException ex) {
			onShutdown();
			Debug.getInstance().debug("Fields.Trace", "End");
		} catch (final IOException ex) {
			ex.printStackTrace();
		}
	}

}
