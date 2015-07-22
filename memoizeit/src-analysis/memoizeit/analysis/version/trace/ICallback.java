package memoizeit.analysis.version.trace;

public interface ICallback {
	
	public void onArguments(int index, long call, int arguments);
	public void onArgument(int arg, int object);
	public void onArgument(int arg, double value);
	public void onArgument(int arg, long value);
	public void onArgumentsDone();
	
	public void onEntry(int index, long call, int target, final String targetType);
	public void onExit(int index, long call, int target, final String targetType);
	public void onExitException(int index, long call, int target, final String targetType);
	
	public void onReturnVoid(int index, long call);
	public void onReturnException(int index, long call);
	public void onReturnObject(int index, long call, int object);
	public void onReturnDouble(int index, long call, double value);
	public void onReturnLong(int index, long call, long value);
	
}
