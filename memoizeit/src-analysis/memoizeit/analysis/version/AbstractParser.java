package memoizeit.analysis.version;

import java.io.StringWriter;

import memoizeit.tuples.serialization.Tags;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

public abstract class AbstractParser {
	
	public final static class ParsingResult {
		public int clazz = Version.INITIAL_MAJOR_VERSION;
		public long call = Long.MIN_VALUE;
		public int type = Integer.MIN_VALUE;
		public int index = Integer.MIN_VALUE;
		public int param = Integer.MIN_VALUE;
		public String version = null;		
	}
	
	protected final StringWriter writerBuffer = new StringWriter(64*1024*1024);

	protected abstract String onCreateVersion(final String buffer) throws Exception;
	protected abstract void onResetState();
	
	protected void onReset() {
		writerBuffer.getBuffer().setLength(0);
		onResetState();
	}
	
	protected abstract int onValue(final JsonReader reader) throws Exception;
	
	public ParsingResult onWrap(final JsonReader reader) throws Exception {
		final ParsingResult result = new ParsingResult();		
		onReset();
		reader.beginObject();
		while (reader.hasNext()) {
			final JsonToken token = reader.peek();
			switch (token) {          
            case NAME:
                final String name = reader.nextName();                                
                if (name.equals(Tags.TYPE)) {
                	result.type = reader.nextInt();
                } else if (name.equals(Tags.CALL)) {
                	result.call = reader.nextLong();
                } else if (name.equals(Tags.INDEX)) {
                	result.index = reader.nextInt();
                } else if (name.equals(Tags.PARAMETER)) {
                	result.param = reader.nextInt();
                } else if (name.equals(Tags.VALUE)) {
            		result.clazz = onValue(reader);
            		result.version = onCreateVersion(writerBuffer.toString());
                }
                break;
            default:
            	reader.skipValue();
            	break;
            }
		}
		reader.endObject();
		
		return result;
	}
	
}
