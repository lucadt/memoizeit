package memoizeit.analysis.version;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import memoizeit.asm.Debug;
import memoizeit.tuples.serialization.Tags;

public final class Method {
	
	public static final class Call {
		
		public static Call create(long index) {
			return new Call(index);
		}
		
		private Call(long index) {
			this.index = index;
			this.arguments = null;
			this.ret = null;
		}
		
		private long index;
		private List<IValue> arguments;
		private IValue ret;
		private IValue target;

		public long getIndex() {
			return index;
		}
		
		private List<IValue> getArgumentsInternal() {
			if (arguments == null) {
				arguments = new LinkedList<IValue>();
			}
			return arguments;
		}

		public List<IValue> getArguments() {
			return getArgumentsInternal();
		}

		public IValue getReturn() {
			return ret;
		}

		public void setReturn(final IValue ret) {
			this.ret = ret;
		}
		
		public final IValue getTarget() {
			return target;
		}

		public final void setTarget(final IValue target) {
			this.target = target;
		}
		
		public String toStringWithStats(
				int retCount,
				List<Integer> argsCount,
				int targetCount) {
			
			String format = "";
			
			if (getTarget() == null) {
				format += "notarget";
			} else {
				if (getTarget().hasVersion() && !getTarget().isNull()) {
					format += String.format("%s(%d)", getTarget().getVersion(), targetCount);	
				} else {
					format += "@target";
				}
			}
			
			format += " ";
			
			if (getReturn() == null) {
				format += "noret";
			} else {
				if (getReturn().hasVersion() && !getReturn().isNull()) {
					format += String.format("%s(%d)", getReturn().getVersion(), retCount);	
				} else {
					format += getReturn().toString();				
				}
			}
			
			if (getArguments().size() > 0) {
				format += ",";
			}
			
			for (int i=0; i < getArguments().size(); i++) {
				
				if (getArguments().get(i).hasVersion() && !getArguments().get(i).isNull()) {
					format += String.format("%s(%d)", getArguments().get(i).getVersion(), argsCount.get(i));	
				} else {
					format += getArguments().get(i).toString();
				}
				
				if (i != getArguments().size() - 1) {
					format += ",";	
				}
				
			}
		
			return format;
			
		}
		
		@Override
		public String toString() {
			
			String format = "";
			
			if (getTarget() == null) {
				format += "notarget";
			} else {
				if (getTarget().hasVersion() && !getTarget().isNull()) {
					format += getTarget().toString();	
				} else {
					format += "@target";
				}
			}
			
			format += " ";
			
			if (getReturn() == null) {
				format += "noret";
			} else {
				format += getReturn().toString();
			}
			
			if (getArguments().size() > 0) {
				format += ",";
			}
			
			for (int i=0; i < getArguments().size(); i++) {
				
				if (i == getArguments().size() - 1) {
					format += getArguments().get(i).toString();
				} else {
					format += getArguments().get(i).toString() + ",";	
				}
				
			}
		
			return format;
			
		}
		
	}
	
	public static interface IValue {
		public Version getVersion();
		public void setVersion(int major, int minor);
		public boolean hasVersion();
		public boolean isNull();
	}
	
	public static final class LongValue implements IValue {

		public static IValue create(long value) {
			return new LongValue(value);
		}
		
		private final long value;
		
		private LongValue(long value) {
			this.value = value;
		}
				
		@Override
		public String toString() {
			return String.format("%d", value);
		}
		
		@Override
		public Version getVersion() {
			throw new UnsupportedOperationException("[LongValue] getVersion()");
		}

		@Override
		public void setVersion(int major, int minor) { 
			throw new UnsupportedOperationException("[LongValue] setVersion()");
		}

		@Override
		public boolean hasVersion() {
			return false;
		}

		@Override
		public boolean isNull() {
			return false;
		}
		
	}
	
	public static final class DoubleValue implements IValue {
		
		public static IValue create(double value) {
			return new DoubleValue(value);
		}
		
		private final double value;
		
		private DoubleValue(double value) {
			this.value = value;
		}
				
		@Override
		public String toString() {
			return String.format("%.5f", value);
		}

		@Override
		public Version getVersion() {
			throw new UnsupportedOperationException("[DoubleValue] getVersion()");
		}

		@Override
		public void setVersion(int major, int minor) { 
			throw new UnsupportedOperationException("[DoubleValue] setVersion()");
		}

		@Override
		public boolean hasVersion() {
			return false;
		}

		@Override
		public boolean isNull() {
			return false;
		}

	}
	
	public static final class VoidValue implements IValue {

		public static IValue create() {
			return new VoidValue();
		}
		
		@Override
		public Version getVersion() {
			throw new UnsupportedOperationException("[VoidValue] getVersion()");
		}

		@Override
		public void setVersion(int major, int minor) { 
			throw new UnsupportedOperationException("[VoidValue] setVersion()");
		}

		@Override
		public boolean hasVersion() {
			return false;
		}

		@Override
		public boolean isNull() {
			return false;
		}
		
		@Override
		public String toString() {
			return "void";
		}

	}
	
	public static final class ExceptionValue implements IValue {

		public static IValue create() {
			return new ExceptionValue();
		}
		
		@Override
		public Version getVersion() {
			throw new UnsupportedOperationException("[ExceptionValue] getVersion()");
		}

		@Override
		public void setVersion(int major, int minor) { 
			throw new UnsupportedOperationException("[ExceptionValue] setVersion()");
		}

		@Override
		public boolean hasVersion() {
			return false;
		}

		@Override
		public boolean isNull() {
			return false;
		}
		
		@Override
		public String toString() {
			return "exception";
		}

	}
	
	public static final class ObjectValue implements IValue {
		
		public static IValue create(int identifier, final String type) {
			return new ObjectValue(identifier, type);
		}
		
		public static IValue create(int identifier) {
			return new ObjectValue(identifier);
		}
		
		private final int identifier;
		private Version version;
		
		private ObjectValue(int identifier) {
			this.identifier = identifier;
		}

		private ObjectValue(int identifier, final String type) {
			this.identifier = identifier;
			if (!isNull()) { this.version = Version.getOrCreateInitial(type); }
		}
		
		@Override
		public String toString() {
			if (hasVersion()) {
				return getVersion().toString();
			} else {
				if (isNull()) {
					return Tags.NULL;
				} else {
					return String.format("@%d", identifier);						
				}
			}		
		}
		
		public int getIdentifier() {
			return identifier;
		}

		@Override
		public Version getVersion() {
			return version;
		}

		@Override
		public void setVersion(int major, int minor) { 
			if (version != null && !version.isType()) {
				Debug.getInstance().debug("Version.ObjectValue", String.format("Has already version %s", version.toString()));
			}
			version = Version.getOrCreate(major, minor);				
		}

		@Override
		public boolean hasVersion() {
			return getVersion() != null;
		}

		@Override
		public boolean isNull() {
			return identifier == 0;
		}
		
	}
	
	public static final Method create(int index) {
		return new Method(index);
	}
	
	private int index;
	private Map<Long, Call> callsMap;
	
	private Method(int index) {
		this.index = index;
	}
	
	private Map<Long, Call> getCallsMapInternal() {
		if (callsMap == null) {
			callsMap = new HashMap<Long, Call>();
		}
		return callsMap;
	}
	
	public int getIndex() {
		return index;
	}

	public Collection<Call> getCalls() {
		return getCallsMapInternal().values();
	}
	
	public void addCall(final long index, final Call call) {
		getCallsMapInternal().put(index, call);
	}
	
	public boolean hasCall(final long call) {
		return getCallsMapInternal().containsKey(call);
	}
	
	public Call getCall(final long call) {
		assert getCallsMapInternal().containsKey(call);
		return getCallsMapInternal().get(call);
	}
	
	public static final int getArgument(final List<Call> calls, int arg) {
		final Set<Integer> objects = new HashSet<Integer>();
		for (final Call call : calls) {
			final IValue argument = call.getArguments().get(arg);
			if (argument instanceof ObjectValue) {
				objects.add(((ObjectValue)argument).getIdentifier());	
			}
		}
		return objects.size();
	}
	
	public static final int getTargetCounts(final List<Call> calls) {
		final Set<Integer> objects = new HashSet<Integer>();
		for (final Call call : calls) {
			final IValue target = call.getTarget();
			if (target instanceof ObjectValue) {
				objects.add(((ObjectValue)target).getIdentifier());	
			}
		}
		return objects.size();
	}
	
	public static final int getReturnCounts(final List<Call> calls) {
		final Set<Integer> objects = new HashSet<Integer>();
		for (final Call call : calls) {
			final IValue ret = call.getReturn();
			if (ret instanceof ObjectValue) {
				objects.add(((ObjectValue)ret).getIdentifier());	
			}
		}
		return objects.size();
	}
	
	public static final class Tuple {
		public String invoke;
		public int count;
	}
		
	public List<Tuple> summarize() {
		
		final Map<String, List<Call>> invokes = getInvokes();
		final List<Tuple> tuples = new LinkedList<Tuple>();

		for (final String invoke : invokes.keySet()) {
			
			final List<Call> calls = invokes.get(invoke);
			final Call firstCall = calls.get(0);
			
			int retCount = getReturnCounts(calls);
			int targetCount = getTargetCounts(calls);
			List<Integer> argsCount = new ArrayList<Integer>();
			for (int i=0; i < firstCall.getArguments().size(); i++) {
				int argCount = getArgument(calls, i);
				argsCount.add(argCount);
			}
			final Tuple tuple = new Tuple();
			tuple.invoke = firstCall.toStringWithStats(retCount, argsCount, targetCount);
			tuple.count = calls.size();
			tuples.add(tuple);
		}
		
		return tuples;
		
	}
	
	public Map<String, List<Call>> getInvokes() {
		final Map<String, List<Call>> invokes = new HashMap<String, List<Call>>();
		for (final Call call : getCalls()) {
			final String invoke = call.toString();
			if (invokes.get(invoke) == null) {
				invokes.put(invoke, new LinkedList<Call>());
			}
			invokes.get(invoke).add(call);
		}
		return invokes;
	}
	
}
