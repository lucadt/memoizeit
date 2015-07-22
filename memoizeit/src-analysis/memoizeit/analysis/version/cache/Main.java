package memoizeit.analysis.version.cache;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import memoizeit.analysis.AbstractMain;
import memoizeit.analysis.version.AbstractParser;
import memoizeit.analysis.version.HashCodeParser;
import memoizeit.analysis.version.JSONParser;
import memoizeit.analysis.version.Method;
import memoizeit.analysis.version.Version;
import memoizeit.analysis.version.AbstractParser.ParsingResult;
import memoizeit.analysis.version.Method.Call;
import memoizeit.analysis.version.trace.Trace;
import memoizeit.analysis.version.trace.TraceBuilder;
import memoizeit.asm.BaseOptions;
import memoizeit.asm.Debug;
import memoizeit.asm.Files;
import memoizeit.tuples.instr.Options;
import memoizeit.tuples.serialization.SerializerConstants;

import com.google.gson.stream.JsonReader;

public final class Main extends AbstractMain {
		
	@Override
	public void onSetup(final String path) { }

	@Override
	public void onDirectory(final String path, final String directory) {		
		final TraceBuilder traceBuilder = new TraceBuilder(profiledMethods);
		new Trace(directory, Files.TUPLES_TRACE, traceBuilder).read();
		Debug.getInstance().debug("Cache.Trace.Main", String.format("%d resolved calls", traceBuilder.resolvedCalls.size()));	
		Debug.getInstance().debug("Cache.Trace.Main", String.format("%d pending calls", traceBuilder.pendingCalls.size()));
		elaborate(directory + '/' + Files.TUPLES_TARGETS);
		System.gc();
		elaborate(directory + '/' + Files.TUPLES_PARAMETERS);
		System.gc();
		elaborate(directory + '/' + Files.TUPLES_RESULTS);
		System.gc();
	}

	private static abstract class AbstractCache {
	
		private static final String createKey(final Call c) {
			
			final StringBuilder sb = new StringBuilder();
				
			if (c.getTarget() == null) {
				sb.append("notarget");
			} else {
				if (c.getTarget().hasVersion() && !c.getTarget().isNull()) {
					sb.append( c.getTarget().toString() );	
				} else {
					sb.append( "@target" );
				}
			}
			sb.append( "--" );
			for (int i=0; i < c.getArguments().size(); i++) {	
				if (i == c.getArguments().size() - 1) {
					sb.append( c.getArguments().get(i).toString() );
				} else {
					sb.append( c.getArguments().get(i).toString() );
					sb.append( "--" );
				}
			}
			
			return sb.toString();
		}
		
		private static final String createValue(final Call c) {
			
			final StringBuilder sb = new StringBuilder();

			if (c.getReturn() == null) {
				sb.append( "noret" );
			} else {
				sb.append( c.getReturn().toString() );
			}
			
			return sb.toString();
		}
		
		public abstract String get(final String key);
		public abstract String put(final String key, final String value);

	}
	
	private void extractTrace() {

		final Set<Integer> methods = profiledMethods.keySet();

		for (final int method : methods) {

			final Method m = profiledMethods.get(method);
			final Map<String, String> cache = new HashMap<String, String>();
			int simpleCallId = 0;

			for (final Call c : m.getCalls()) {

				final String key = AbstractCache.createKey(c);
				final String value = AbstractCache.createValue(c);

				final String cacheValue = cache.get(key); 
				if (cacheValue == null) {
					cache.put(key, value);
					System.err.println(method +  ",m," + key + "," + value + "," + simpleCallId + "," + c.getIndex() + "," + cache.size() );
				} else {
					System.err.println(method +  ",h," + key + "," + value + "," + simpleCallId + "," + c.getIndex() + "," + cache.size() );				
				}
				simpleCallId++;
			}
		}
	}
		
	@Override
	public void onShutdown(final String path) {
		extractTrace();
	}
	
	private final Map<Integer, Method> profiledMethods;
	private final AbstractParser parser;
	
	public Main() {
		profiledMethods = new HashMap<Integer, Method>();
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
				ParsingResult result = parser.onWrap(reader);
	    		 createVersion(result);
			} catch (final InterruptedException ex) {
				ex.printStackTrace();
			} catch (final Exception ex) {
				ex.printStackTrace();
			}
			count++;
		}
		reader.endArray();
		Debug.getInstance().debug("Version.Cache.Main", String.format("Parsed %d wraps", count));
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
				Debug.getInstance().debug("Version.Cache.Main", String.format("Lookup of call %d for %d failed", result.index, result.call));
			}
			
		} else {
			Debug.getInstance().debug("Version.Cache.Main", String.format("Lookup of method %d failed", result.index));
			System.exit(-1);
		}
			
	}
				
	public static void main(String[] args) {
		new Main().onMain(BaseOptions.getTuplesDirectory());
	}

}
