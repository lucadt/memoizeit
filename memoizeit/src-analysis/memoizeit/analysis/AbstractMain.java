package memoizeit.analysis;

import java.io.File;

import memoizeit.asm.Files;

public abstract class AbstractMain {
	
	public abstract void onSetup(final String path);
	public abstract void onDirectory(final String path, final String directory);
	public abstract void onShutdown(final String path);

	public void onMain(final String path) {
		onSetup(path);
		final File directory = new File(path + '/' + Files.DATA_DIR);
		for (final File file : directory.listFiles()) {
			if (file.isDirectory()) {
				onDirectory(path, file.getAbsolutePath());
			}
		}
		onShutdown(path);
	}

}
