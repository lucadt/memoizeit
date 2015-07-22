package memoizeit.asm.profiler;

import java.io.File;
import java.io.IOException;

import memoizeit.asm.profiler.IAnalysisHook;

public abstract class AbstractFileProfiler implements IAnalysisHook {
	
	private String fileName;
	private String filePath;
	private File fileHandler;
	
	public AbstractFileProfiler(final String filePath, final String fileName) {
		this.fileName = fileName;
		this.filePath = filePath;
		this.fileHandler = null;
	}
	
	public File getFileHandler() {
		return fileHandler;
	}
	
	private void createFilePath() {
		new File(filePath).mkdirs();
	}
	
	private void createFileHandler() {
		fileHandler = new File(filePath + '/' + fileName);
		if (!fileHandler.exists()) {
			try {
				fileHandler.createNewFile();
			} catch (final IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	@Override
	public void onStart() {
		createFilePath();
		createFileHandler();
	}

	@Override
	public void onShutdown() {
		fileHandler = null;		
	}

}
