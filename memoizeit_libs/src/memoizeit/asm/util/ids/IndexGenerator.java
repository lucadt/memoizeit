package memoizeit.asm.util.ids;


public final class IndexGenerator {
	
	private static long NEXT_INDEX = Long.MIN_VALUE;
	
	public static synchronized final long getNextIndex() {
		long value = NEXT_INDEX++;
		if (value != Long.MAX_VALUE) {
			return value;			
		} else {
			throw new RuntimeException("[IndexGenerator] Reached Long.MAX_VALUE");
		}
	}
	
}
