package memoizeit.tuples.serialization;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

import memoizeit.asm.Debug;
import memoizeit.asm.TargetAccesses;
import memoizeit.asm.TargetAccesses.Accesses;
import memoizeit.asm.util.AsmHelper;
import memoizeit.tuples.instr.Options;

public abstract class AbstractSerializerHelper {
	
	public abstract void onObjectType(final String aString) throws Exception;
	public abstract void onObjectStart(final Object anObject) throws Exception;
	public abstract void onObjectField(final String aString) throws Exception;
	public abstract void onObjectEnd() throws Exception;
	public abstract void onObjectTypeEnd() throws Exception;

	public abstract void onNull() throws Exception;
	public abstract void onHash(final Object anObject) throws Exception;
	public abstract void onLink(final Object anObject) throws Exception;
	public abstract void onSpecial(final Object anObject) throws Exception;

	public abstract void onString(final String aString) throws Exception;
	public abstract void onNumber(final Number aNumber) throws Exception;
	public abstract void onBoolean(boolean aBoolean) throws Exception;
	public abstract void onByte(byte aByte) throws Exception;
	public abstract void onInteger(int anInt) throws Exception;
	public abstract void onLong(long aLong) throws Exception;
	public abstract void onShort(short aShort) throws Exception;
	public abstract void onChar(char aChar) throws Exception;
	public abstract void onFloat(float aFloat) throws Exception;
	public abstract void onDouble(double aDouble) throws Exception;
	
	public abstract void onObject(final Object anObject) throws Exception;
	public abstract void onTarget(final Object anObject, int index) throws Exception;
	public abstract void onDispatch(final Object anObject, int index) throws Exception;
	public abstract void onTraverseField(final Object anObject, int depth, final String aFieldName) throws Exception;
	
	private static boolean MAXIMUM_DEPTH_REACHED = false;
	public static synchronized final void onMaximumDepthReached() { MAXIMUM_DEPTH_REACHED = true; }
	public static synchronized final boolean isMaximumDepthReached() { return MAXIMUM_DEPTH_REACHED; }
	
	public static final boolean isMaximumDepthReached(int depth) { 
		if (Options.useMaxDepth() && Options.logDepth()) {
			DepthLogger.getInstance().onDepth(depth);
		}
		return Options.useMaxDepth() && depth >= Options.getMaxDepth(); 
	}
	
	public static final void checkMaximumDepthReached(int depth) {
		depth = depth == 0 ? 1 : depth;
		if (isMaximumDepthReached(depth)) { 
			onMaximumDepthReached(); 
		} 
	}
		
	protected boolean isVisited(final Object anObject) {
		if (anObject instanceof ObjectTag) {
			final ObjectTag aTag = (ObjectTag) anObject;
			return aTag.isVisited();
		} else {
			return identityVisited.get().get(anObject) != null;
		}
	}
	
	protected void onVisit(final Object anObject) { 
		if (anObject instanceof ObjectTag) {
			final ObjectTag aTag = (ObjectTag) anObject;
			aTag.onVisit();
		}
		identityVisited.get().put(anObject, ObjectTag.VISITED);
	}
	
	protected void onResetVisited() {
		for (final Object anObject : identityVisited.get().keySet()) {
			if (anObject instanceof ObjectTag) {
				final ObjectTag aTag = (ObjectTag) anObject;
				aTag.onReset();
			}
		}
		identityVisited.get().clear();
	}

	private static final ThreadLocal<IdentityHashMap<Object, Integer>> identityVisited = new ThreadLocal<IdentityHashMap<Object, Integer>>() {
		protected IdentityHashMap<Object,Integer> initialValue() {
			return new IdentityHashMap<Object, Integer>(4096);
		}
	};
				
	protected int getIdentifier(final Object anObject) {  return System.identityHashCode(anObject);  }
	protected void onReset() { onResetVisited(); }

	private final Comparator<Field> fieldComparator = new Comparator<Field>() {
		@Override
		public int compare(final Field arg0, final Field arg1) {
			return arg0.getName().compareTo(arg1.getName());
		}
	};
	
	public final void onReflectionSuper(final Object obj, int depth, final String startType) throws Exception {
        Class<? extends Object> thisClazz = obj.getClass();
        while (thisClazz != null) {
        	if (startType.equals(thisClazz.getName())) { break; }
        	thisClazz = thisClazz.getSuperclass();
        }
        onDispatchReflection(obj, depth, thisClazz);
	}
	
	protected static final boolean isTraversable(final Field field) {
		final int modifiers = field.getModifiers();
		final String name = field.getName();
		return !(Modifier.isStatic(modifiers) || 
				AsmHelper.isArtificial(name) || 
				name.equals("memoizeit_thr_loc_id") ||
				name.equals("memoizeit_thr_loc_metadata") );
	}

	protected final void onDispatchField(final Class<? extends Object> thisClazz, final Field field, final Object anObject, int depth) throws Exception {
		try {
			field.setAccessible(true);
			onTraverseField(field.get(anObject), depth, thisClazz.getName() + '.' + field.getName());
		} catch (final SecurityException ex) {
			ex.printStackTrace();
		} catch (final IllegalArgumentException ex) {
			ex.printStackTrace();
		} catch (final IllegalAccessException ex) {
			ex.printStackTrace();
		}
	}
	
	protected final void onDispatchReflection(final Object anObject, int depth, final Class<? extends Object> intialClass) throws Exception {
		Class<? extends Object> thisClazz = intialClass;
		while (thisClazz != null) {
			final Field[] fields = thisClazz.getDeclaredFields();
			Arrays.sort(fields, fieldComparator);
			for (final Field field : fields) {
				if (isTraversable(field)) {
					onDispatchField(thisClazz, field, anObject, depth);
				}
			}
			thisClazz = thisClazz.getSuperclass();
		}
	}
	
	protected final void onAdjustFields(final Accesses accesses, final Class<? extends Object> aClazz) {
		Class<? extends Object> thisClazz = aClazz;
		accesses.setAdjusted(false);
		while (thisClazz != null) {
			if (accesses.has(thisClazz.getName())) {				
				onAdjustField(accesses, thisClazz);
			}
			thisClazz = thisClazz.getSuperclass();
		}
		accesses.setAdjusted(true);
	}

	protected final void onAdjustField(final Accesses accesses, final Class<? extends Object> thisClazz) {
		final Set<String> aFields = accesses.get(thisClazz.getName());
		final Field[] fields = thisClazz.getDeclaredFields();
		final List<String> vFields = new ArrayList<String>(aFields);
		for (final Field field : fields) {
			if (isTraversable(field)) {
				if (vFields.contains(field.getName())) {
					vFields.remove(field.getName());
				}
			}
		}
		Debug.getInstance().debug("MemoizeIt.AbstractSerializerHelper", "Adjusting: " + vFields.size());
		if (vFields.size() > 0) {
			for (final String vField : vFields) {
				Class<? extends Object> tmpClazz = thisClazz.getSuperclass();
				boolean found = false;
				while (tmpClazz != null && !found) {
					final Field[] tmpFields = tmpClazz.getDeclaredFields();
					for (final Field tmpField : tmpFields) {
						if (isTraversable(tmpField) && vField.equals(tmpField.getName())) {
							aFields.remove(vField);
							accesses.add(tmpClazz.getName(), vField);
							Debug.getInstance().debug("MemoizeIt.AbstractSerializerHelper", "Moving " + vField + " from " + thisClazz.getName() + " to " + tmpClazz.getName());
							found = true;
							break;
						}
					}
					tmpClazz = tmpClazz.getSuperclass();
				}
				if (!found) {
					Debug.getInstance().debug("MemoizeIt.AbstractSerializerHelper", vField + " not found in hierarchy of " + thisClazz.getName());
				}
			}
		}
	}
	
	protected final void onDispatchReflectionTarget(final Object anObject, int depth, int index) throws Exception {		
		Class<? extends Object> thisClazz = anObject.getClass();
		final Accesses accesses = TargetAccesses.getInstance().get(index, thisClazz.getName());
		if (accesses != null && !accesses.isAdjusted()) {
			onAdjustFields(accesses, thisClazz);
		}
		while (thisClazz != null) {
			final Field[] fields = thisClazz.getDeclaredFields();
			Arrays.sort(fields, fieldComparator);
			if (accesses != null && accesses.has(thisClazz.getName())) {
				final Set<String> aFields = accesses.get(thisClazz.getName());
				for (final Field field : fields) {
					if (isTraversable(field) && aFields.contains(field.getName())) {
						onDispatchField(thisClazz, field, anObject, depth);
					}
				}
			} else {
				for (final Field field : fields) {
					if (isTraversable(field)) {
						onDispatchField(thisClazz, field, anObject, depth);
					}
				}
			}
			thisClazz = thisClazz.getSuperclass();
		}
	}

}
