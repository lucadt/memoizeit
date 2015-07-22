package memoizeit.asm.profiler.raw;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import memoizeit.asm.BaseOptions;
import memoizeit.asm.profiler.AbstractFileProfiler;

public abstract class AbstractRawPipedFileWriter extends AbstractFileProfiler implements Runnable {

	public static final int BUFFER_SIZE = 4*1024;
	
	private final byte[] my_empty_buffer = new byte[0];

	private LinkedBlockingQueue<byte[]> pipe = null;
	private DataOutputStream stream = null;
	private OutputStream out = null;
	private Thread thread = null;

	public AbstractRawPipedFileWriter(final String filePath, final String fileName) {
		super(filePath, fileName);
	}
	
	protected void createOutput() {
		try {
			if (BaseOptions.useCompressedStream()) {
				final Deflater def = new Deflater();
				def.setLevel(Deflater.BEST_SPEED);
				out = new DeflaterOutputStream(new FileOutputStream(getFileHandler()), def, 2 * BUFFER_SIZE);				
			} else {
				out = new BufferedOutputStream(new FileOutputStream(getFileHandler()), 2 * BUFFER_SIZE);
			}
		} catch (final IOException ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
		createOutput();
		pipe = new LinkedBlockingQueue<byte[]>(1024);
		stream = new DataOutputStream(new BufferedOutputStream(new PipeOutputStream(pipe), BUFFER_SIZE));
		thread = new Thread(this, "[Pipe.Write] " + Thread.currentThread().getName() );
		thread.setDaemon(true);
		thread.start();
	}
	
	@Override
	public void onShutdown() {
		if (getStream() != null) {
			try {
				stream.flush();
				stream.close();
				pipe.put(my_empty_buffer);
				thread.join();
				out.flush();
				out.close();
			} catch (final IOException ex) {
				ex.printStackTrace();
			} catch (final InterruptedException ex) {
				ex.printStackTrace();
			}
		}
		super.onShutdown();
	}
	
	@Override
	public void run() {
		try {
			byte[] buffer = pipe.take();
			while (buffer != my_empty_buffer) {
				out.write(buffer, 0, buffer.length);
				buffer = pipe.take();
			}
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public DataOutputStream getStream() {
		return stream;
	}
	
	public class PipeOutputStream extends OutputStream {

		final LinkedBlockingQueue<byte[]> pipe;
		
		public PipeOutputStream(final LinkedBlockingQueue<byte[]> pipe) {
			this.pipe = pipe;
		}

		@Override
		public void write(final int b) throws IOException {
			write(new byte[] { (byte) b });
		}

		@Override
		public void write(final byte[] b) throws IOException {
			write(b, 0, b.length);
		}

		@Override
		public void write(final byte[] b, final int off, final int len) throws IOException {	
			final byte[] nBuffer = new byte[len];
			System.arraycopy(b, off, nBuffer, 0, len);
			try {
				pipe.put(nBuffer);
			} catch (final InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}

}
