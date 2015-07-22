package memoizeit.tuples.serialization.convert;

import memoizeit.tuples.serialization.AbstractSerializerHelper;
import memoizeit.tuples.serialization.Tags;

public final class ArrayFastConverter implements Converter {
	
	private static final ThreadLocal<Converter> INSTANCE = new ThreadLocal<Converter>() {
		protected Converter initialValue() {
			return new ArrayFastConverter();
		};
	};
	
	public static Converter getInstance() {
		return INSTANCE.get();
    }
	
    public void convert(final AbstractSerializerHelper helper, int depth, final Object anObject) throws Exception {
    	helper.onObjectType(Tags.ARRAY);
    	helper.onObjectStart(anObject);
    	if (anObject instanceof boolean[]) {
        	final boolean[] anArray = (boolean[]) anObject;
    		for (int i=0; i < anArray.length; i++) {
            	final boolean item = anArray[i];
            	helper.onObjectField(Tags.ENTRY);
       			helper.onBoolean(item);
        	}
    	} else if (anObject instanceof byte[]) {
        	final byte[] anArray = (byte[]) anObject;
    		for (int i=0; i < anArray.length; i++) {
            	final byte item = anArray[i];
            	helper.onObjectField(Tags.ENTRY);
       			helper.onByte(item);
        	}
    	} else if (anObject instanceof short[]) {
        	final short[] anArray = (short[]) anObject;
    		for (int i=0; i < anArray.length; i++) {
            	final short item = anArray[i];
            	helper.onObjectField(Tags.ENTRY);
       			helper.onShort(item);
        	}
    	} else if (anObject instanceof char[]) {
        	final char[] anArray = (char[]) anObject;
    		for (int i=0; i < anArray.length; i++) {
            	final char item = anArray[i];
            	helper.onObjectField(Tags.ENTRY);
       			helper.onChar(item);
        	}
    	} else if (anObject instanceof int[]) {
        	final int[] anArray = (int[]) anObject;
    		for (int i=0; i < anArray.length; i++) {
            	final int item = anArray[i];
            	helper.onObjectField(Tags.ENTRY);
       			helper.onInteger(item);
        	}
    	} else if (anObject instanceof long[]) {
        	final long[] anArray = (long[]) anObject;
    		for (int i=0; i < anArray.length; i++) {
            	final long item = anArray[i];
            	helper.onObjectField(Tags.ENTRY);
       			helper.onLong(item);
        	}
    	} else if (anObject instanceof float[]) {
        	final float[] anArray = (float[]) anObject;
    		for (int i=0; i < anArray.length; i++) {
            	final float item = anArray[i];
            	helper.onObjectField(Tags.ENTRY);
       			helper.onFloat(item);
        	}
    	} else if (anObject instanceof double[]) {
        	final double[] anArray = (double[]) anObject;
    		for (int i=0; i < anArray.length; i++) {
            	final double item = anArray[i];
            	helper.onObjectField(Tags.ENTRY);
       			helper.onDouble(item);
        	}
    	} else {
        	final Object[] anArray = (Object[]) anObject;
    		for (int i=0; i < anArray.length; i++) {
            	final Object item = anArray[i];
            	helper.onObjectField(Tags.ENTRY);
       			helper.onDispatch(item, depth);
        	}
    	}
   		helper.onObjectEnd();
   		helper.onObjectTypeEnd();
    }

}
