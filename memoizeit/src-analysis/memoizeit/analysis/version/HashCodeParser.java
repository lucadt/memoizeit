package memoizeit.analysis.version;

import java.io.IOException;

import memoizeit.analysis.version.Version.Repository;
import memoizeit.asm.Debug;
import memoizeit.tuples.serialization.Tags;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

public class HashCodeParser extends AbstractParser {

	private int onHashCode(final JsonReader reader) throws IOException {
		boolean unreadValue = true;
		int clazz = Version.INITIAL_MAJOR_VERSION;
		reader.beginObject();
		while (reader.hasNext()) {
			final JsonToken token = reader.peek();
			switch (token) {          
            case NAME:
            	final String name = reader.nextName();
            	if (name.equals(Tags.HASH)) {
            		unreadValue = false;
            		writerBuffer.append(reader.nextString());
            	} else if (name.equals(Tags.TYPE)) {
            		clazz = Repository.getOrCreateClassVersion(reader.nextString());
            	}
            	break;
            default:
            	reader.skipValue();
            	Debug.getInstance().debug("HashCodeParser", "Error skipping while parsing @hash_code");
    			throw new RuntimeException();
			}
		}
		reader.endObject();
		if (unreadValue) {
			Debug.getInstance().debug("HashCodeParser", "Parsing hash_code with null hash");
			throw new RuntimeException();
		}
		return clazz;
	}
	
	@Override
	protected int onValue(final JsonReader reader) throws Exception {
		int clazz = Version.INITIAL_MAJOR_VERSION;
		reader.beginObject();
		while (reader.hasNext()) {
			final JsonToken token = reader.peek();
			switch (token) {
			case NAME:
            	final String name = reader.nextName();
            	if (name.equals(Tags.NULL)) {
            		reader.nextNull();
					clazz = Version.NULL_MAJOR_VERSION;
				} else if (name.equals(Tags.HASH_CODE)) {
					clazz = onHashCode(reader);
            	}
            	break;
            default:
            	reader.skipValue();
            	Debug.getInstance().debug("HashCodeParser", "Error skipping while parsing @value");
    			throw new RuntimeException();
			}
		}
		reader.endObject();
		return clazz;
	}
	
	@Override
	protected String onCreateVersion(final String buffer) throws Exception {
		return buffer;
	}

	@Override
	protected void onResetState() { }

}
