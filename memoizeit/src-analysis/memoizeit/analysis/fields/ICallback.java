package memoizeit.analysis.fields;

public interface ICallback {
	public void onEntry(int index, int target, final String targetType);
	public void onExit(int index, int target);
	public void onExitException(int index, int target);
	public void onFieldRead(int index, int field, int target);
	public void onFieldStaticRead(int index, int field);
	public void onFieldStaticWrite(int index, int field);
	public void onFieldWrite(int index, int field, int target);
}
