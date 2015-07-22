package memoizeit.asm.util.ids;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public abstract class AbstractIdManager {
	
	private final Set<String> ELEMENTS;
	
	protected AbstractIdManager() {
		this.ELEMENTS = new HashSet<String>();
	}
	
	public final synchronized void onImport(final String fileName) {
		try {
			final BufferedReader reader = new BufferedReader(new FileReader(fileName));
			final Scanner scanner = new Scanner(reader);
			while(scanner.hasNextLine()) {
				final String line = scanner.nextLine();
				if (line.length() > 0) {
					getIndex(line.trim());
				}
			}
			scanner.close();
		} catch (final FileNotFoundException ex) {
			ex.printStackTrace();
		}
	}
	
	public final synchronized String onExport() {
		final StringBuilder buffer = new StringBuilder();
		for(final String element : ELEMENTS) {
			buffer.append(element + '\n');
		}
		return buffer.toString();
	}
	
	public final synchronized int getIndex(final String owner, final String name, final String descriptor) {
		return getIndex(getSignature(owner, name, descriptor));
	}
	
	public final synchronized int getIndex(final String signature) {
		ELEMENTS.add(signature);
		return signature.hashCode();
	}
	
	public final synchronized String getSignature(int index) {
		for(final String element : ELEMENTS) {
			if (element.hashCode() == index) {
				return element;
			}
		}
		return null;
	}
	
	public abstract String getSignature(final String owner, final String name, final String descriptor);
	
}
