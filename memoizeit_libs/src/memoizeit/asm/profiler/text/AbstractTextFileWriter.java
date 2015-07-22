package memoizeit.asm.profiler.text;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import memoizeit.asm.profiler.AbstractFileProfiler;

public abstract class AbstractTextFileWriter extends AbstractFileProfiler {
	
	private PrintWriter out;
	private boolean append;
	
	public AbstractTextFileWriter(final String filePath, final String fileName) {
		this(filePath, fileName, false);
	}
	
	public AbstractTextFileWriter(final String filePath, final String fileName, boolean append) {
		super(filePath, fileName);
		this.append = append;
	}

	protected PrintWriter getOutput() {
		return out;
	}
	
	protected void createOutput() {
		try {
			out = new PrintWriter(new FileWriter(getFileHandler(), append));	
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
		if (out != null) {
			out.flush();
			out.close();
		}
		super.onShutdown();
	}	
}
