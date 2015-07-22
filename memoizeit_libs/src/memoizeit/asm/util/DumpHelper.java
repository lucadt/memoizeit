package memoizeit.asm.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

public final class DumpHelper {
	
	public static final String filterThreadName(final String name) {
		return name.replace(' ', '_').replace('.', '_').replace(':', '_').replace('/', '_');
	}
	
	public static final String getThreadIdentifier(long identifier, final String name) {
		return String.format("thread_%d_%s", identifier, filterThreadName(name));
	}
	
	public static void dumpByteCode(final String className, final byte[] classData, final String baseDirectory) {
		try {
			final int lastSeparatorIndex = className.lastIndexOf('/');
			if (lastSeparatorIndex > 0) {
				final String path = baseDirectory + '/' + className.substring(0, lastSeparatorIndex + 1);
				final File classFilePath = new File(path);
				if (!classFilePath.exists()) {
					classFilePath.mkdirs();
				}
			}
			
			final File classFile = new File(baseDirectory + '/' + className + ".class");
			
			if (!classFile.exists()) {
				classFile.createNewFile();
			}
			
			final FileOutputStream fos = new FileOutputStream(classFile);
		 	fos.write(classData);
		 	fos.flush();
		    fos.close();

		} catch (final FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (final IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public static void writeStringToFile(final String fileName, final String data) {
		final File outputFile = new File(fileName);
		if (!outputFile.exists()) {
			try {
				outputFile.createNewFile();
			} catch (final IOException ex) {
				ex.printStackTrace();
			}	
		}
		try {
			final PrintWriter outputWriter = new PrintWriter(new FileWriter(outputFile));	
			outputWriter.println(data);
			outputWriter.flush();
			outputWriter.close();	
		} catch (final IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public static void writeObjectToFile(final String fileName, final Object data) {
		final File outputFile = new File(fileName);
		if (!outputFile.exists()) {
			try {
				outputFile.createNewFile();
			} catch (final IOException ex) {
				ex.printStackTrace();
			}	
		}
		try {
			final ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(outputFile));
			out.writeObject(data);
			out.close();
		} catch (final IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public static Object readObjectFromFile(final String fileName) {
		try {
			final ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName));
			final Object object = in.readObject();
			in.close();
			return object;
		} catch (final IOException ex) {
			ex.printStackTrace();
		} catch (final ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		return null;
	}
}
