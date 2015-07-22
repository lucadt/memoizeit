package memoizeit.asm;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;

import memoizeit.asm.profiler.IAnalysisHook;

public class Manager {
		
	private final ReentrantLock lock;
	private final IdentityHashMap<Thread, IAnalysisHook> appThreadReferences;
	
	protected Manager() {
		lock = new ReentrantLock();
		appThreadReferences = new IdentityHashMap<Thread, IAnalysisHook>();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					Thread.sleep(5000);
				} catch (final InterruptedException ex) {
					ex.printStackTrace();
				}
				onPreProcessThreads();
				onProcessThreads();
				onPostProcessThreads();
			}
		});
	}
	
	public void register(final Thread thread, final IAnalysisHook profiler) {
		lock.lock();
		try {
			final IAnalysisHook previousAnalysis = appThreadReferences.put(thread, profiler);
			if (previousAnalysis != null) {
				Debug.getInstance().debug("Manager", "Thread " + thread + " already registered.");
				System.exit(-5);
			}
			profiler.onStart();
		} finally {
			lock.unlock();
		}
	}
	
	protected synchronized void onProcessThreads() {
		lock.lock();
		try {
			final Iterator<Entry<Thread, IAnalysisHook>> iter = appThreadReferences.entrySet().iterator();
			while (iter.hasNext()) {	
				final Entry<Thread, IAnalysisHook> entry = iter.next();
				final Thread thread = entry.getKey();
				final IAnalysisHook profiler = entry.getValue();
				System.err.println("memoizeit.asm.Manager -- Processing thread " + thread.getName());
				onProcessThread(thread, profiler);
			}
		} finally {			
			lock.unlock();
		}
	}
	
	protected void onProcessThread(final Thread thread, final IAnalysisHook profiler) {
		profiler.onShutdown();
	}
	
	protected void onPreProcessThreads() { }
	
	protected void onPostProcessThreads() { 
		Debug.getInstance().onShutdown();
	}
	
}
