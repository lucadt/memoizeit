package memoizeit.tuples.profiler.trace;

public interface TraceConstants {
	public static final byte ON_ENTRY = 0xa;
	public static final byte ON_EXIT = 0xb;
	public static final byte ON_EXIT_EXCEPTION = 0xc;
	public static final byte ON_ARGUMENTS = 0xd;
	public static final byte ON_RETURN_VALUE = 0xe;
	public static final byte TYPE_LONG = 0x1;
	public static final byte TYPE_DOUBLE = 0x2;
	public static final byte TYPE_OBJECT = 0x3;
	public static final byte TYPE_VOID = 0x4;
	public static final byte TYPE_EXCEPTION = 0x5;
}
