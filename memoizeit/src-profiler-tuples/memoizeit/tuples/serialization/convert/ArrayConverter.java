package memoizeit.tuples.serialization.convert;

import java.lang.reflect.Array;

import memoizeit.tuples.serialization.AbstractSerializerHelper;
import memoizeit.tuples.serialization.Tags;

public final class ArrayConverter implements Converter {
	
	private static final ThreadLocal<Converter> INSTANCE = new ThreadLocal<Converter>() {
		protected Converter initialValue() {
			return new ArrayConverter();
		};
	};
	
	public static Converter getInstance() {
		return INSTANCE.get();
    }
	
    public void convert(final AbstractSerializerHelper helper, int depth, final Object anObject) throws Exception {
    	helper.onObjectType(Tags.ARRAY);
    	helper.onObjectStart(anObject);
		int length = Array.getLength(anObject);
		for (int i=0; i < length; i++) {
        	final Object item = Array.get(anObject, i);
        	helper.onObjectField(Tags.ENTRY);
   			helper.onDispatch(item, depth);
    	}
   		helper.onObjectEnd();
   		helper.onObjectTypeEnd();
    }

}
