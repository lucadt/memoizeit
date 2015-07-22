package memoizeit.tuples.serialization;

public class ObjectTag {
	
	protected static final int INVALID = -1;
	protected static final int VISITED = 1;
	protected static final int DEFAULT = 0;
	
	private static ThreadLocal<Integer> MEMOIZEIT_THR_LOC_VISIT_COUNTER = new ThreadLocal<Integer>() {
		protected Integer initialValue() {
			return INVALID;
		};
	};
	
	public static void resetGlobalCounter() {
		MEMOIZEIT_THR_LOC_VISIT_COUNTER.set(INVALID);
	}	
	
	public static void incrementGlobalCounter() {
		int value = MEMOIZEIT_THR_LOC_VISIT_COUNTER.get() + 1;
		MEMOIZEIT_THR_LOC_VISIT_COUNTER.set(value);
	}
	
	public static int getGlobalCounter() {
		return MEMOIZEIT_THR_LOC_VISIT_COUNTER.get();
	}
	
	public final void onReset() {
		if (memoizeit_thr_loc_metadata == null) initCounter();
		memoizeit_thr_loc_metadata.set(INVALID);
		if (memoizeit_thr_loc_id == null) initIdentifier();
		memoizeit_thr_loc_id.set(INVALID);
	}

	private ThreadLocal<Integer> memoizeit_thr_loc_id = null;

	private final synchronized void initIdentifier() {
		memoizeit_thr_loc_id = new ThreadLocal<Integer>() {
			protected Integer initialValue() {
				return INVALID;
			};
		};
	}
	
	public final boolean hasIdentifier() {
		if (memoizeit_thr_loc_id == null) initIdentifier();
		assert isVisitedFast();
		return getIdentifier() != INVALID;
	}
	
	public final int getIdentifier() {
		if (memoizeit_thr_loc_id == null) initIdentifier();
		return memoizeit_thr_loc_id.get();
	}
	
	public final void setIdentifier(final int value) {
		if (memoizeit_thr_loc_id == null) initIdentifier();
		memoizeit_thr_loc_id.set(value);
	}
	
	private ThreadLocal<Integer> memoizeit_thr_loc_metadata = null;

	private final synchronized void initCounter() {
		memoizeit_thr_loc_metadata = new ThreadLocal<Integer>() {
			protected Integer initialValue() {
				return INVALID;
			};
		};
	}
	
	public final void onVisitFast() {
		if (memoizeit_thr_loc_metadata == null) initCounter();
		if (!isVisitedFast()) {
			setIdentifier(INVALID);
			memoizeit_thr_loc_metadata.set(getGlobalCounter());		
		}
	}
	
	public final boolean isVisitedFast() {
		if (memoizeit_thr_loc_metadata == null) initCounter();
		return memoizeit_thr_loc_metadata.get() == getGlobalCounter();
	}
	
	public final void onVisit() {
		if (memoizeit_thr_loc_metadata == null) initCounter();
		memoizeit_thr_loc_metadata.set(VISITED);		
	}
	
	public final boolean isVisited() {
		if (memoizeit_thr_loc_metadata == null) initCounter();
		return memoizeit_thr_loc_metadata.get() == VISITED;
	}
	
}
