package memoizeit.field.profiler;

import java.io.IOException;

import memoizeit.asm.Files;
import memoizeit.asm.profiler.raw.AbstractRawPipedFileWriter;

public class TraceProfiler extends AbstractRawPipedFileWriter {
	
	public static final byte ON_ENTRY = 0x1;
	public static final byte ON_EXIT = 0x2;
	public static final byte ON_EXCEPTION = 0x3;
	public static final byte ON_FIELD_READ = 0x4;
	public static final byte ON_FIELD_WRITE = 0x5;
	public static final byte ON_STATIC_FIELD_READ = 0x6;
	public static final byte ON_STATIC_FIELD_WRITE = 0x7;

	public TraceProfiler(final String filePath) {
		super(filePath, Files.FIELDS_TRACE);
	}
		
	public void onFieldStaticRead(int index, int field) {
		try {
			getStream().writeByte(ON_STATIC_FIELD_READ);
			getStream().writeInt(index);
			getStream().writeInt(field);
		} catch (final IOException ex) {
			ex.printStackTrace();
		}
	}
		
	public void onFieldRead(int index, int field, final Object object) {
		final int identifier = System.identityHashCode(object);
		try {
			getStream().writeByte(ON_FIELD_READ);
			getStream().writeInt(index);
			getStream().writeInt(field);
			getStream().writeInt(identifier);
		} catch (final IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public void onFieldStaticWrite(int index, int field) {
		try {
			getStream().writeByte(ON_STATIC_FIELD_WRITE);
			getStream().writeInt(index);
			getStream().writeInt(field);
		} catch (final IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public void onFieldWrite(int index, int field, final Object object) {
		final int identifier = System.identityHashCode(object);
		try {
			getStream().writeByte(ON_FIELD_WRITE);
			getStream().writeInt(index);
			getStream().writeInt(field);
			getStream().writeInt(identifier);
		} catch (final IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public void onEntry(int index, final Object object) {
		final int identifier = System.identityHashCode(object);
		try {
			getStream().writeByte(ON_ENTRY);
			getStream().writeInt(index);
			getStream().writeInt(identifier);
			if (object != null) {
				getStream().writeUTF(object.getClass().getName());
			}
		} catch (final IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public void onExit(int index, final Object object) {
		final int identifier = System.identityHashCode(object);
		try {
			getStream().writeByte(ON_EXIT);
			getStream().writeInt(index);
			getStream().writeInt(identifier);
		} catch (final IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public void onException(int index, final Object object) {
		try {
			getStream().writeByte(ON_EXCEPTION);
			getStream().writeInt(index);
		} catch (final IOException ex) {
			ex.printStackTrace();
		}
	}
	
}
