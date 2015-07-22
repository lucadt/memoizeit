package memoizeit.tuples.serialization.convert;

import memoizeit.tuples.serialization.AbstractSerializerHelper;

public interface Converter {
    public void convert(final AbstractSerializerHelper helper, int depth, final Object anObject) throws Exception;
}
