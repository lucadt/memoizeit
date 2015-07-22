package memoizeit.asm.profiler.raw;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import memoizeit.asm.BaseOptions;
import memoizeit.asm.profiler.AbstractFileProfiler;

public class AbstractRawPipedFileReader extends AbstractFileProfiler implements Runnable {

	public static final int BUFFER_SIZE = 4*1024;	
	private final byte[] my_empty_buffer = new byte[0];
	
	private LinkedBlockingQueue<byte[]> pipe = null;
	private Thread thread = null;
	private DataInputStream stream = null;
	private InputStream in = null;

	public AbstractRawPipedFileReader(final String filePath, final String fileName) {
		super(filePath, fileName);
	}
	
	protected void createInput() {
		if (BaseOptions.useCompressedStream()) {
			try {
				in = new InflaterInputStream(new FileInputStream(getFileHandler()), new Inflater(), BUFFER_SIZE);
			} catch (final FileNotFoundException ex) {
				ex.printStackTrace();
			}	
		} else {
			try {
				in = new BufferedInputStream(new FileInputStream(getFileHandler()), BUFFER_SIZE);
			} catch (final FileNotFoundException ex) {
				ex.printStackTrace();
			}	
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
		createInput();
		pipe = new LinkedBlockingQueue<byte[]>(1024);
		stream = new DataInputStream(new PipeInputStream(pipe));
		thread = new Thread(this, "[Pipe.Read] " + Thread.currentThread().getName() );
		thread.setDaemon(true);
		thread.start();
	}
	
	@Override
	public void onShutdown() {
		if (getStream() != null) {
			try {
				thread.join();
				stream.close();
				in.close();
			} catch (final InterruptedException ex) {
				ex.printStackTrace();
			} catch (final IOException ex) {
				ex.printStackTrace();
			}
		}
		super.onShutdown();
	}
	
	protected DataInputStream getStream() {
		return stream;
	}

	@Override
	public void run() {
		try {
			byte[] buffer = new byte[128*BUFFER_SIZE];
			int length = 0;
			while ((length = in.read(buffer)) != -1) {
				final byte[] data = new byte[length];
				System.arraycopy(buffer, 0, data, 0, length);
				pipe.put(data);
			}
			pipe.put(my_empty_buffer);
		} catch (final IOException ex) {
			ex.printStackTrace();
		} catch (final InterruptedException ex) {
			ex.printStackTrace();
		}
	}
	
	public class PipeInputStream extends InputStream {

		final LinkedBlockingQueue<byte[]> pipe;
		
		int position;
		byte[] buffer;
		
		public PipeInputStream(final LinkedBlockingQueue<byte[]> pipe) {
			this.pipe = pipe;
			this.position = -1;
			this.buffer = null;
		}
		
		private int getNext() {
			return buffer[position++] & 0xff;
		}

		@Override
		public int read() throws IOException {
			if (position >= 0 && position < buffer.length) {
				return getNext();
			}
			try {
				final byte[] buffer = pipe.take();
				if (buffer == my_empty_buffer) {
					this.buffer = null;
					this.position = -1;
					return -1;
				}
				this.buffer = buffer;
				this.position = 0;
				return getNext();
			} catch (final InterruptedException ex) {
				ex.printStackTrace();
			}
			return -1;
		}
	
	}

}
