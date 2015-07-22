package memoizeit.asm.profiler.raw;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import memoizeit.asm.BaseOptions;
import memoizeit.asm.profiler.AbstractFileProfiler;

public abstract class AbstractRawFileWriter extends AbstractFileProfiler {
	
	public static final int BUFFER_SIZE = 4*1024;
	
	private DataOutputStream out = null;
	
	public AbstractRawFileWriter(final String filePath, final String fileName) {
		super(filePath, fileName);
	}
	
	protected DataOutputStream getStream() {
		return out;
	}

	protected void createOutput() {
		try {
			if (BaseOptions.useCompressedStream()) {
				final Deflater def = new Deflater();
				def.setLevel(Deflater.BEST_SPEED);
				out = new DataOutputStream(new DeflaterOutputStream(new FileOutputStream(getFileHandler()), def, BUFFER_SIZE));				
			} else {
				final FileOutputStream fos = new FileOutputStream(getFileHandler());
				final BufferedOutputStream bos = new BufferedOutputStream(fos, BUFFER_SIZE);
				out = new DataOutputStream(bos);
			}
		} catch (final IOException ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
		createOutput();
	}
	
	@Override
	public void onShutdown() {
		if (getStream() != null) {
			try {
				getStream().flush();
				getStream().close();
			} catch (final IOException ex) {
				ex.printStackTrace();
			}
		}
		super.onShutdown();
	}
	
}
