package memoizeit.analysis.version;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import memoizeit.analysis.AbstractMain;
import memoizeit.analysis.version.AbstractParser.ParsingResult;
import memoizeit.analysis.version.Method.Call;
import memoizeit.analysis.version.trace.Trace;
import memoizeit.analysis.version.trace.TraceBuilder;
import memoizeit.asm.BaseOptions;
import memoizeit.asm.Debug;
import memoizeit.asm.Files;
import memoizeit.asm.profiler.text.AbstractTextFileWriter;
import memoizeit.tuples.instr.Options;
import memoizeit.tuples.serialization.SerializerConstants;

import com.google.common.collect.Table;
import com.google.gson.stream.JsonReader;

public final class Main extends AbstractMain {
		
	@Override
	public void onSetup(final String path) {
		versionerThread.start();
	}

	@Override
	public void onDirectory(final String path, final String directory) {		
		final TraceBuilder traceBuilder = new TraceBuilder(profiledMethods);
		new Trace(directory, Files.TUPLES_TRACE, traceBuilder).read();
		Debug.getInstance().debug("Version.Main", String.format("%d resolved calls", traceBuilder.resolvedCalls.size()));	
		Debug.getInstance().debug("Version.Main", String.format("%d pending calls", traceBuilder.pendingCalls.size()));
		elaborate(directory + '/' + Files.TUPLES_TARGETS);
		System.gc();
		elaborate(directory + '/' + Files.TUPLES_PARAMETERS);
		System.gc();
		elaborate(directory + '/' + Files.TUPLES_RESULTS);
		System.gc();
	}

	@Override
	public void onShutdown(final String path) {
		versionerThread.interrupt();
		try {
			versionerThread.join();
		} catch (final InterruptedException ex) {
			ex.printStackTrace();
		}
		final Output output = new Output(path);
		output.onStart();
		output.print(profiledMethods);
		output.onShutdown();
		
		final Statistics outStats = new Statistics(path, Files.STATS_FILE);
		outStats.onStart();
		for (Table.Cell<Integer, String, Integer> cell : Version.VERSIONS.cellSet()) {
			outStats.write(cell.getRowKey() + " "  + cell.getValue() + " " + cell.getColumnKey());
		}
		outStats.onShutdown();
		
	}
	
	private static final class Statistics extends AbstractTextFileWriter {

		public Statistics(final String filePath, final String fileName) {
			super(filePath, fileName);
		}
		
		public void write(final String output) {
			getOutput().println(output);
			getOutput().flush();
		}
		
	}
	
	private final Map<Integer, Method> profiledMethods;
	private final BlockingQueue<AbstractParser.ParsingResult> versionerQueue;
	private final Thread versionerThread;
	private final AbstractParser parser;
	
	public Main() {
		profiledMethods = new HashMap<Integer, Method>();
		versionerQueue = new LinkedBlockingQueue<AbstractParser.ParsingResult>();
		versionerThread = new Thread(new Versioner());
		if (Options.useCustomHashCode()) {
			parser = new HashCodeParser();
		} else {
			parser = new JSONParser();
		}
	}	
	
	public void elaborate(final String structureFile) {
		final int bufferSize = 1024*1024;
		JsonReader reader = null;
		if (BaseOptions.useCompressedStream()) {
			try {
				final Inflater def = new Inflater();
				final FileInputStream fis = new FileInputStream(structureFile);
				final InflaterInputStream iis = new InflaterInputStream(fis, def, bufferSize);
				reader = new JsonReader(new InputStreamReader(iis));
			} catch (final FileNotFoundException ex) {
				ex.printStackTrace();
			}
		} else {
			try {
				final FileReader fr = new FileReader(structureFile);
				final BufferedReader br = new BufferedReader(fr, bufferSize);
				reader = new JsonReader(br);
			} catch (final FileNotFoundException ex) {
				ex.printStackTrace();
			}
		}
		
		try {
			onWraps(reader);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void onWraps(final JsonReader reader) throws IOException {
		int count = 0;
		reader.beginArray();
		while (reader.hasNext()) {
			try {
				versionerQueue.put(parser.onWrap(reader));
			} catch (final InterruptedException ex) {
				ex.printStackTrace();
			} catch (final Exception ex) {
				ex.printStackTrace();
			}
			count++;
		}
		reader.endArray();
		Debug.getInstance().debug("Version.Main", String.format("Parsed %d wraps", count));
    }
	
	public void createVersion(final ParsingResult result) {
		int classVersion = Version.INITIAL_MAJOR_VERSION; 
		int classHashVersion = Version.INITIAL_MAJOR_VERSION;
		if (result.clazz != Version.NULL_MAJOR_VERSION) {
			classVersion = result.clazz;
			classHashVersion = Version.getOrCreateMinor(classVersion, result.version);					
		} else {
			classVersion = classHashVersion = Version.NULL_MAJOR_VERSION; 
		}
		
		if (profiledMethods.containsKey(result.index)) {

			final Method method = profiledMethods.get(result.index);
			
			if (method.hasCall(result.call)) {
				
				final Call aCall = method.getCall(result.call);
				
				switch (result.type) {
				
				case SerializerConstants.TYPE_PARAMETER:
					aCall.getArguments().get(result.param).setVersion(classVersion, classHashVersion);
					break;
				case SerializerConstants.TYPE_RETURN:
					aCall.getReturn().setVersion(classVersion, classHashVersion);
					break;
				case SerializerConstants.TYPE_TARGET:
					aCall.getTarget().setVersion(classVersion, classHashVersion);						
					break;
				}
				
			} else {
				Debug.getInstance().debug("Version.Main", String.format("Lookup of call %d for %d failed", result.index, result.call));
			}
			
		} else {
			Debug.getInstance().debug("Version.Main", String.format("Lookup of method %d failed", result.index));
			System.exit(-1);
		}
			
	}
	
	private final class Versioner implements Runnable {
		@Override
		public void run() {
			int counter = 0;
		     try {
		    	 while (true) { 
		    		 final ParsingResult result = versionerQueue.take();
		    		 createVersion(result);
		    		 counter++;
		    	 }
		     } catch (final InterruptedException ex) {
		    	 Debug.getInstance().debug("Version.Versioner", "Exiting");
		    	 if (!versionerQueue.isEmpty()) {
		    		 while (true) {
		    			 final ParsingResult result = versionerQueue.poll();		    			 
		    			 if (result != null) {
		    				 createVersion(result);
		    				 counter++;
		    			 } else {
		    				 break;
		    			 } 
		    		 } 
		    	 }
		    	 Debug.getInstance().debug("Version.Versioner", "Versioned " + counter);
		    	 Debug.getInstance().debug("Version.Versioner", "Exited");
		     }	
		}	
	}
			
	public static void main(String[] args) {
		new Main().onMain(BaseOptions.getTuplesDirectory());
	}

}
