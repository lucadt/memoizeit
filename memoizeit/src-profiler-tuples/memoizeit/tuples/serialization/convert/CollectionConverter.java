package memoizeit.tuples.serialization.convert;

import java.util.Collection;
import java.util.Iterator;

import memoizeit.tuples.serialization.AbstractSerializerHelper;
import memoizeit.tuples.serialization.Tags;

public final class CollectionConverter implements Converter {
	
	private static final ThreadLocal<Converter> INSTANCE = new ThreadLocal<Converter>() {
		protected Converter initialValue() {
			return new CollectionConverter();
		};
	};
	
	public static Converter getInstance() {
		return INSTANCE.get();
    }
	
	public static boolean canConvert(final String type) {
        return type.equals("java.util.Collection") ||
        	   type.equals("java.util.List") ||
        	   type.equals("java.util.Set") ||
        	   type.equals("java.util.ArrayList") ||
        	   type.equals("java.util.LinkedList") ||
        	   type.equals("java.util.HashSet") ||
        	   type.equals("java.util.TreeSet") ||
        	   type.equals("java.util.LinkedHashSet") ||
        	   type.equals("java.util.Vector");
	}
	
	@SuppressWarnings("rawtypes")
    public void convert(final AbstractSerializerHelper helper, int depth, final Object anObject) throws Exception {
		final Collection collection = (Collection) anObject;
    	helper.onObjectType(Tags.COLLECTION);
    	helper.onObjectStart(collection);
   		for (Iterator iterator = collection.iterator(); iterator.hasNext();) {
   			helper.onObjectField(Tags.ENTRY);
   			helper.onDispatch(iterator.next(), depth);
    	}
   		helper.onObjectEnd();
   		helper.onObjectTypeEnd();
    }
			  
}
