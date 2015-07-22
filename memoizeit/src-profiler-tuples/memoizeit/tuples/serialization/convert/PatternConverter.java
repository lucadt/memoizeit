package memoizeit.tuples.serialization.convert;

import java.io.IOException;
import java.util.regex.Pattern;

import memoizeit.tuples.serialization.AbstractSerializerHelper;
import memoizeit.tuples.serialization.Tags;

public final class PatternConverter implements Converter {

	private static final ThreadLocal<Converter> INSTANCE = new ThreadLocal<Converter>() {
		protected Converter initialValue() {
			return new PatternConverter();
		};
	};
	
	public static Converter getInstance() {
		return INSTANCE.get();
    }
	
	public static boolean canConvert(final String type) {
		return type.equals("java.util.regex.Pattern");
    }

    public void convert(final AbstractSerializerHelper helper, int depth, final Object anObject) throws Exception {
    	try {
    		final Pattern pattern = (Pattern) anObject;
        	helper.onObjectType(Tags.PATTERN);
        		helper.onString(pattern.pattern() + ':' + pattern.flags());
       		helper.onObjectTypeEnd();
		} catch (final IOException ex) {
			ex.printStackTrace();
		}	
    }	
}
