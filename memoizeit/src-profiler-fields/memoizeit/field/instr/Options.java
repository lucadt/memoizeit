package memoizeit.field.instr;

import memoizeit.asm.BaseOptions;
import memoizeit.asm.Files;

public final class Options {
		
	public static String getDataDirectory() {
		return BaseOptions.getFieldsDirectory()  + '/' + Files.DATA_DIR;
	}
	
	public static String getMethodsFile() {
		return BaseOptions.getFieldsDirectory() + '/' + Files.METHODS_FILE;
	}
	
	public static String getFieldsFile() {
		return BaseOptions.getFieldsDirectory() + '/' + Files.FIELDS_FILE;
	}
	
	public static String getClassesFile() {
		return BaseOptions.getFieldsDirectory() + '/' + Files.CLASSES_FILE;
	}
		
}
