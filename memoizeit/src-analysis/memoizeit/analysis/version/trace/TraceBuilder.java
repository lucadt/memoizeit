package memoizeit.analysis.version.trace;

import java.util.HashMap;
import java.util.Map;

import memoizeit.analysis.version.Method;
import memoizeit.analysis.version.Method.Call;
import memoizeit.analysis.version.Method.DoubleValue;
import memoizeit.analysis.version.Method.LongValue;
import memoizeit.analysis.version.Method.ObjectValue;
import memoizeit.analysis.version.Method.VoidValue;
import memoizeit.asm.Debug;

public final class TraceBuilder implements ICallback {
	
	public final Map<Long, Call> pendingCalls = new HashMap<Long, Call>();
	public final Map<Long, Call> resolvedCalls = new HashMap<Long, Call>();
	public final Map<Integer, Method> methods;
	
	public TraceBuilder(Map<Integer, Method> methods) {
		this.methods = methods;
	}
	
	private Call thisCall = null;
	
	public Map<Integer, Method> getMethods() {
		return methods;
	}
	
	public Call resolveCall(long callIndex) {
		Call call = null;
		if (pendingCalls.containsKey(callIndex)) {
			call = pendingCalls.remove(callIndex);
			resolvedCalls.put(callIndex, call);
		}
		return call;
	}
	
	public Method getMethod(int index) {
		Method method = null;
		if (methods.containsKey(index)) {
			method = methods.get(index);
		} else {
			method = Method.create(index);
			methods.put(index, method);
		}
		return method;
	}
	
	private void onErrorMessage(int index, long call) {
		Debug.getInstance().debug("Version.TraceBuilder", String.format("Not existing pending call %d %d", index, call));
	}
	
	private void onIgnoreMessage(int index, long call) {
		Debug.getInstance().debug("Version.TraceBuilder", String.format("Ignoring exceptional return %d %d", index, call));
	}
		
	@Override
	public void onEntry(int index, long callIndex, int target, final String targetType) {
		thisCall = Call.create(callIndex);
		thisCall.setTarget(ObjectValue.create(target, targetType));
		pendingCalls.put(callIndex, thisCall);
	}
	
	@Override
	public void onArguments(int index, long callIndex, int arguments) { 
		// TODO Not necessary.
	}
	
	@Override
	public void onArgument(int arg, int object) {
		thisCall.getArguments().add(ObjectValue.create(object));
	}

	@Override
	public void onArgument(int arg, double value) {
		thisCall.getArguments().add(DoubleValue.create(value));
	}

	@Override
	public void onArgument(int arg, long value) {
		thisCall.getArguments().add(LongValue.create(value));
	}

	@Override
	public void onArgumentsDone() {
		thisCall = null;
	}
	
	@Override
	public void onExit(int index, long call, int target, final String targetType) { 
		// TODO Not necessary.
	}
	
	@Override
	public void onExitException(int index, long call, int target, final String targetType) { 
		onIgnoreMessage(index, call);
	}
	
	@Override
	public void onReturnVoid(int index, long call) {
		final Call aCall = resolveCall(call);
		if (aCall != null) {
			getMethod(index).addCall(call, aCall);
			aCall.setReturn(VoidValue.create());
		} else {
			onErrorMessage(index, call);
		}
	}

	@Override
	public void onReturnObject(int index, long call, int object) {
		final Call aCall = resolveCall(call);
		if (aCall != null) {
			getMethod(index).addCall(call, aCall);
			aCall.setReturn(ObjectValue.create(object));			
		} else {
			onErrorMessage(index, call);
		}
	}

	@Override
	public void onReturnDouble(int index, long call, double value) {
		final Call aCall = resolveCall(call);
		if (aCall != null) {	
			getMethod(index).addCall(call, aCall);
			aCall.setReturn(DoubleValue.create(value));		
		} else {
			onErrorMessage(index, call);
		}
	}

	@Override
	public void onReturnLong(int index, long call, long value) {
		final Call aCall = resolveCall(call);
		if (aCall != null) {
			getMethod(index).addCall(call, aCall);
			aCall.setReturn(LongValue.create(value));
		} else {
			onErrorMessage(index, call);
		}
	}

	@Override
	public void onReturnException(int index, long call) {
		// TODO Not necessary.
	}

}
