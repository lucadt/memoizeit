package memoizeit.tuples.serialization;

import org.objectweb.asm.commons.Method;


public interface Methods {
			
	public final Method TRAVERSER_MY_TRAVERSE = Method.getMethod("void myTraverse(memoizeit.tuples.serialization.AbstractSerializerHelper,int)");
	public final Method GENERATOR_NEXT_INDEX = Method.getMethod("long getNextIndex()");
	
	public final Method HELPER_ON_SUPER_REFLECTION = Method.getMethod("void onReflectionSuper(java.lang.Object,int,java.lang.String)");
	public final Method HELPER_ON_OBJECT_FIELD = Method.getMethod("void onObjectField(java.lang.String)");
	public final Method HELPER_ON_TRAVERSE_FIELD = Method.getMethod("void onTraverseField(java.lang.Object,int,java.lang.String)");

	public final Method HELPER_ON_BOOLEAN = Method.getMethod("void onBoolean(boolean)");
	public final Method HELPER_ON_BYTE = Method.getMethod("void onByte(byte)");
	public final Method HELPER_ON_INT = Method.getMethod("void onInteger(int)");
	public final Method HELPER_ON_LONG = Method.getMethod("void onLong(long)");
	public final Method HELPER_ON_SHORT = Method.getMethod("void onShort(short)");
	public final Method HELPER_ON_FLOAT = Method.getMethod("void onFloat(float)");
	public final Method HELPER_ON_DOUBLE = Method.getMethod("void onDouble(double)");
	public final Method HELPER_ON_CHAR = Method.getMethod("void onChar(char)");
	
	public final Method PROFILER_ON_METHOD_ENTRY = Method.getMethod("void onMethodEntry(int,long,java.lang.Object)");
	public final Method PROFILER_ON_METHOD_EXIT = Method.getMethod("void onMethodExit(int,long,java.lang.Object)");
	public final Method PROFILER_ON_METHOD_EXIT_EXCEPTION = Method.getMethod("void onMethodException(int,long,java.lang.Object)");	
	public final Method PROFILER_ON_ARGUMENTS = Method.getMethod("void onArguments(int,long,int)");
	public final Method PROFILER_ON_RETURN_VALUE = Method.getMethod("void onReturnValue(int,long)");
	public final Method PROFILER_ON_VALUE_DOUBLE = Method.getMethod("void onValue(double)");
	public final Method PROFILER_ON_VALUE_LONG = Method.getMethod("void onValue(long)");
	public final Method PROFILER_ON_VALUE_OBJECT = Method.getMethod("void onObject(java.lang.Object)");
	public final Method PROFILER_ON_VALUE_VOID = Method.getMethod("void onVoid()");
	public final Method PROFILER_ON_VALUE_EXCEPTION = Method.getMethod("void onException()");

	public final Method PROFILER_ON_DUMP_TARGET = Method.getMethod("void onDumpTarget(java.lang.Object,byte,int,int,long)");
	public final Method PROFILER_ON_DUMP_NULL = Method.getMethod("void onDumpNull(byte,int,int,long)");
	public final Method PROFILER_ON_DUMP_ARRAY = Method.getMethod("void onDumpArray(java.lang.Object[],byte,int,int,long)");
	public final Method PROFILER_ON_DUMP_STRING = Method.getMethod("void onDumpString(java.lang.String,byte,int,int,long)");
	public final Method PROFILER_ON_DUMP_OBJECT = Method.getMethod("void onDumpObject(java.lang.Object,byte,int,int,long)");
	public final Method PROFILER_ON_DUMP_REFLECTION = Method.getMethod("void onDumpReflection(java.lang.Object,byte,int,int,long)");
	public final Method PROFILER_ON_DUMP_COLLECTION = Method.getMethod("void onDumpCollection(java.lang.Object,byte,int,int,long)");
	public final Method PROFILER_ON_DUMP_MAP = Method.getMethod("void onDumpMap(java.lang.Object,byte,int,int,long)");
	public final Method PROFILER_ON_DUMP_FILE = Method.getMethod("void onDumpFile(java.lang.Object,byte,int,int,long)");
	public final Method PROFILER_ON_DUMP_PATTERN = Method.getMethod("void onDumpPattern(java.lang.Object,byte,int,int,long)");

}
