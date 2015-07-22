package memoizeit.asm.profiler.raw;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import memoizeit.asm.BaseOptions;
import memoizeit.asm.profiler.AbstractFileProfiler;


public class AbstractRawFileReader extends AbstractFileProfiler {
	
	public static final int BUFFER_SIZE = 4*1024;
	
	private DataInputStream in;
	
	public AbstractRawFileReader(final String filePath, final String fileName) {
		super(filePath, fileName);
	}
	
	protected DataInputStream getStream() {
		return in;
	}

	protected void createInput() {
		if (BaseOptions.useCompressedStream()) {
			try {
				in = new DataInputStream(new InflaterInputStream(new FileInputStream(getFileHandler()), new Inflater(), BUFFER_SIZE));
			} catch (final FileNotFoundException ex) {
				ex.printStackTrace();
			}	
		} else {
			try {
				in = new DataInputStream(new BufferedInputStream(new FileInputStream(getFileHandler()), BUFFER_SIZE));
			} catch (final FileNotFoundException ex) {
				ex.printStackTrace();
			}	
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
		createInput();
	}
	
	@Override
	public void onShutdown() {
		if (getStream() != null) {
			try {
				getStream().close();
			} catch (final IOException ex) {
				ex.printStackTrace();
			}
		}
		super.onShutdown();
	}

}
