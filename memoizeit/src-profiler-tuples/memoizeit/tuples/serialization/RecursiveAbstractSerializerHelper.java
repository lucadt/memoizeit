package memoizeit.tuples.serialization;

import memoizeit.tuples.serialization.convert.ArrayFastConverter;
import memoizeit.tuples.serialization.convert.CollectionConverter;
import memoizeit.tuples.serialization.convert.Converter;
import memoizeit.tuples.serialization.convert.FileConverter;
import memoizeit.tuples.serialization.convert.MapConverter;
import memoizeit.tuples.serialization.convert.PatternConverter;

public abstract class RecursiveAbstractSerializerHelper extends AbstractSerializerHelper {
	
	@Override
	public void onObject(final Object anObject) throws Exception {
		final int depth = 0;
		onDispatch(anObject, depth);
	}
	
	@Override
	public void onTarget(final Object anObject, int index) throws Exception {
		final int depth = 0;
		onVisit(anObject);
 		onObjectType(Tags.OBJECT);
		onObjectStart(anObject);
		onDispatchReflectionTarget(anObject, depth+1, index);
		onObjectEnd();
		onObjectTypeEnd();
	}
	
	@Override
	public void onTraverseField(final Object anObject, int depth, final String aFieldName) throws Exception {
		onObjectField(aFieldName);
		onDispatch(anObject, depth);
	}
	
	@Override
	public void onDispatch(final Object anObject, int depth) throws Exception {
		if (anObject != null) {
			final Class<? extends Object> aClass = anObject.getClass();
			final String aClassName = aClass.getName();
			if (anObject instanceof ITraverser) {
				onCheckDepth(anObject, depth);
			} else if (aClass.isArray()) {
				onCheckDepthConverter(anObject, depth, ArrayFastConverter.getInstance());	
			} else if (CollectionConverter.canConvert(aClassName)) {
				onCheckDepthConverter(anObject, depth, CollectionConverter.getInstance());
			} else if (MapConverter.canConvert(aClassName)) {
				onCheckDepthConverter(anObject, depth, MapConverter.getInstance());
			} else if (FileConverter.canConvert(aClassName)) {
				onCheckDepthConverter(anObject, depth, FileConverter.getInstance());
			} else if (PatternConverter.canConvert(aClassName)) {
				onCheckDepthConverter(anObject, depth, PatternConverter.getInstance());
			} else if (anObject instanceof String) {
				checkMaximumDepthReached(depth);
				onString((String) anObject);
			} else if (anObject instanceof StringBuffer) {
				checkMaximumDepthReached(depth);
				final StringBuffer buffer = (StringBuffer) anObject;
				onString(buffer.toString());
			} else if (anObject instanceof StringBuilder) {
				checkMaximumDepthReached(depth);
				final StringBuilder builder = (StringBuilder) anObject;
				onString(builder.toString());
			} else if (anObject instanceof Boolean || aClass.equals(boolean.class)) {
				checkMaximumDepthReached(depth);
				onBoolean((Boolean) anObject);
			} else if (anObject instanceof Character || aClass.equals(char.class)) {
				checkMaximumDepthReached(depth);
				onChar((Character) anObject);
			} else if (anObject instanceof Number || aClass.isPrimitive()) {
				checkMaximumDepthReached(depth);
				onNumber((Number) anObject);
			} else {
				onCheckDepthReflection(anObject, depth);
			}
		} else {
			checkMaximumDepthReached(depth);
			onNull();
		}
	}
	
	public void onCheckDepth(final Object anObject, int depth) throws Exception {
		if (isVisited(anObject)) {
			onLink(anObject);
		} else {
			if (isMaximumDepthReached(depth)) {
				onMaximumDepthReached();
				onHash(anObject);
			} else {
				onVisit(anObject);
				onTraverse(anObject, depth);
			}
		}
	}
	
	public void onCheckDepthReflection(final Object anObject, int depth) throws Exception {
		if (isVisited(anObject)) {
			onLink(anObject);
		} else {
			if (isMaximumDepthReached(depth)) {
				onMaximumDepthReached();
				onHash(anObject);
			} else {
				onVisit(anObject);
				onReflection(anObject, depth);
			}
		}
	}
	
	public void onCheckDepthConverter(final Object anObject, int depth, final Converter aConverter) throws Exception {
    	if (isVisited(anObject)) {
    		onLink(anObject);
    	} else {
			if (isMaximumDepthReached(depth)) {
				onMaximumDepthReached();
				onHash(anObject);
			} else {
				onVisit(anObject);
				aConverter.convert(this, depth+1, anObject);           		
			}	
		}
	}
	
	public void onTraverse(final Object anObject, int depth) throws Exception {
		final ITraverser traverse = (ITraverser) anObject;
		onObjectType(Tags.OBJECT);
		onObjectStart(anObject);
		traverse.myTraverse(this, depth+1);
		onObjectEnd();
        onObjectTypeEnd();
	}
	
	public void onReflection(final Object anObject, int depth) throws Exception {
		final Class<? extends Object> thisClazz = anObject.getClass();
		onObjectType(Tags.OBJECT);
		onObjectStart(anObject);
		onDispatchReflection(anObject, depth+1, thisClazz);
		onObjectEnd();
		onObjectTypeEnd();
   }
		
}