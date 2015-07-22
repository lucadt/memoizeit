package memoizeit.tuples.serialization.convert;

import java.util.Iterator;
import java.util.Map;

import memoizeit.tuples.serialization.AbstractSerializerHelper;
import memoizeit.tuples.serialization.Tags;

public final class MapConverter implements Converter {
	
	private static final ThreadLocal<Converter> INSTANCE = new ThreadLocal<Converter>() {
		protected Converter initialValue() {
			return new MapConverter();
		};
	};
	
	public static Converter getInstance() {
		return INSTANCE.get();
    }
	
    public static boolean canConvert(final String type) {
        return type.equals("java.util.Map") ||
        	   type.equals("java.util.HashMap") ||
        	   type.equals("java.util.Hashtable") ||
        	   type.equals("java.util.TreeMap") ||
        	   type.equals("java.util.LinkedHashMap") || 
               type.equals("java.util.concurrent.ConcurrentHashMap") || 
               type.equals("sun.font.AttributeMap");
    }
    
    @SuppressWarnings("rawtypes")
    public void convert(final AbstractSerializerHelper helper, int depth, final Object anObject) throws Exception {
		final Map map = (Map) anObject;
		helper.onObjectType(Tags.MAP);
    	helper.onObjectStart(map);
   		for (Iterator iterator = map.entrySet().iterator(); iterator.hasNext();) {
			final Map.Entry entry = (Map.Entry) iterator.next();
			helper.onObjectField(Tags.ENTRY);
	    	helper.onObjectType(null);
   				helper.onObjectField(Tags.MAP_KEY);
   				helper.onDispatch(entry.getKey(), depth);
				helper.onObjectField(Tags.MAP_VALUE);
				helper.onDispatch(entry.getValue(), depth);
   	   		helper.onObjectTypeEnd();
    	}
   		helper.onObjectEnd();
   		helper.onObjectTypeEnd();
    }

}
