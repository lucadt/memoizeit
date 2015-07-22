package memoizeit.asm.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.Type;

public final class Util {
	
	public static final Map<String, Type> TYPE_MAP = createTypeMap();
	
	private static Map<String, Type> createTypeMap() {
        final Map<String, Type> result = new HashMap<String, Type>();
        result.put("java.lang.Double",Type.DOUBLE_TYPE);
        result.put("java.lang.Float",Type.FLOAT_TYPE);
        result.put("java.lang.Boolean",Type.BOOLEAN_TYPE);
        result.put("java.lang.Long",Type.LONG_TYPE);
        result.put("java.lang.Integer",Type.INT_TYPE);
        result.put("java.lang.Character",Type.CHAR_TYPE);
        result.put("java.lang.Byte",Type.BYTE_TYPE);
        result.put("java.lang.Short",Type.SHORT_TYPE);
        return Collections.unmodifiableMap(result);
    }
	
	public static boolean isString(final String type) {
		return type.equals("java.lang.String") || type.equals("java/lang/String");
	}
	
	public static boolean isStringBufferBuilder(final String type) {
		return type.equals("java.lang.StringBuffer") || type.equals("java/lang/StringBuffer") ||
			   type.equals("java.lang.StringBuilder") || type.equals("java/lang/StringBuilder");
	}
	
	public static boolean isPrimitive(final String type) {
		   return type.equals("java.lang.Integer") ||
				  type.equals("java.lang.Boolean") ||
				  type.equals("java.lang.Float") ||
				  type.equals("java.lang.Double") ||
				  type.equals("java.lang.Long") ||
				  type.equals("java.lang.Character") ||
				  type.equals("java.lang.Byte") ||
				  type.equals("java.lang.Short");
	}
	
	public static final boolean isPrimitive(final Class<?> clazz) {
		return clazz.isPrimitive() || isPrimitive(clazz.getName());
	}
	
	public static boolean isJavaCoreClass(final String className) {
		return className.startsWith("com/apple") || className.startsWith("com.apple") ||
		className.startsWith("com/sun") || className.startsWith("com.sun") ||
		className.startsWith("apple") ||
		className.startsWith("java") ||
		className.startsWith("javax") ||
		className.startsWith("sun") ||
		className.startsWith("sunw") ||
		className.startsWith("org/omg") || className.startsWith("org.omg") ||
		className.startsWith("org/ietf") || className.startsWith("org.ietf") ||
		className.startsWith("org/jcp") || className.startsWith("org.jcp") ||
		className.startsWith("org/relaxng") || className.startsWith("org.relaxng") ||
		className.startsWith("org/w3c") || className.startsWith("org.w3c") ||
		className.startsWith("org/xml") || className.startsWith("org.xml");
	}
		
	public static boolean isDacapoClass(final String className) {
		return className.startsWith("dacapo") || 
			   className.startsWith("org/dacapo") || className.startsWith("org.dacapo") ||
			   className.equals("Harness");
	}
	
	public static boolean isLibraryClass(final String className) {
		return className.startsWith("m/lib/instr") || className.startsWith("m.lib.instr");
	}
	
	public static boolean isInstrumentationClass(final String className) {
		return className.startsWith("memoizeit") || className.startsWith("memoizeit");
	}
	
	public static boolean isNotToProfile(final String className) {
		return 	isJavaCoreClass(className) || 
				isDacapoClass(className) || 
				isLibraryClass(className) ||
				isInstrumentationClass(className);
	}
	
	public static boolean isToProfile(final String className) {
		return !isNotToProfile(className);
	}
	
}
	