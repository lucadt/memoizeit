package memoizeit.tuples.serialization.convert;

import java.io.File;
import java.io.IOException;

import memoizeit.tuples.serialization.AbstractSerializerHelper;
import memoizeit.tuples.serialization.Tags;

public final class FileConverter implements Converter {
	
	private static final ThreadLocal<Converter> INSTANCE = new ThreadLocal<Converter>() {
		protected Converter initialValue() {
			return new FileConverter();
		};
	};
	
	public static Converter getInstance() {
		return INSTANCE.get();
    }

	public static boolean canConvert(final String type) {
		return type.equals("java.io.File");
    }

    public void convert(final AbstractSerializerHelper helper, int depth, final Object anObject) throws Exception {
    	try {
        	helper.onObjectType(Tags.FILE);
        		helper.onString(((File) anObject).getPath());
       		helper.onObjectTypeEnd();
		} catch (final IOException ex) {
			ex.printStackTrace();
		}
    }
    
}
