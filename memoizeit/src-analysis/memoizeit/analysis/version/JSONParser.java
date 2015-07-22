package memoizeit.analysis.version;

import java.util.HashMap;
import java.util.Map;

import memoizeit.analysis.version.Version.Repository;
import memoizeit.asm.Debug;
import memoizeit.asm.util.ids.Mapping;
import memoizeit.tuples.serialization.Tags;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public final class JSONParser extends AbstractParser {
	
	private JsonWriter writer;
	private Mapping nodeMapping;
	
	private int currentFieldIdentifier;
	private final Map<String, Integer> fieldIdentifiers = new HashMap<String, Integer>();

	private int getOrCreateFieldIdentifier(final String fieldName) {
		if (!fieldIdentifiers.containsKey(fieldName)) {
			fieldIdentifiers.put(fieldName, currentFieldIdentifier++);
		}
		return fieldIdentifiers.get(fieldName);
	}
	
	private int onPrimitive(final JsonReader reader) throws Exception {
		boolean unreadValue = true;
		reader.beginObject();
		writer.beginObject();
		while (reader.hasNext()) {
			final JsonToken token = reader.peek();
			switch (token) {
			case NAME:
            	final String name = reader.nextName();
            	if (name.equals(Tags.DATA)) {
            		final JsonToken dataToken = reader.peek();
            		switch (dataToken) {
            		case STRING:
                    case NUMBER:
                    	unreadValue = false;
                		writer.name(Tags.PRIMITIVE).value(reader.nextString());
                        break;
                    case BOOLEAN:
                    	unreadValue = false;
                        boolean b = reader.nextBoolean();
                		writer.name(Tags.PRIMITIVE).value(Boolean.toString(b));
                        break;
                    case NULL:
                    	unreadValue = false;
                        reader.nextNull();
                        Debug.getInstance().debug("JSONParser", "Parsing primitive with null value");
            			throw new RuntimeException();
            		default:
            			reader.skipValue();
            			break;
            		}
            	}
            	break;
            
            default:
            	reader.skipValue();
            	Debug.getInstance().debug("JSONParser", "Error skipping while parsing @primitive");
			}
		}
		writer.endObject();
		reader.endObject();

		if (unreadValue) {
			Debug.getInstance().debug("JSONParser", "Parsing primitive with null data");
			throw new RuntimeException();
		}
		
		return Repository.getOrCreateClassVersion(Tags.PRIMITIVE);
	}
	
	private int onArray(final JsonReader reader) throws Exception {
		int id = Integer.MIN_VALUE;
		boolean unreadValue = true;
		reader.beginObject();
		writer.beginObject();
		while (reader.hasNext()) {
			final JsonToken token = reader.peek();
			switch (token) {
			case NAME:
            	final String name = reader.nextName();
            	if (name.equals(Tags.ID)) {
            		id = nodeMapping.getOrCreateMapping(reader.nextInt());
            		unreadValue = false;
            	} else if (name.equals(Tags.TYPE)) {
            		reader.nextString();
            	} else if (name.equals(Tags.ENTRY)) {
            		assert id != Integer.MIN_VALUE;
            		writer.name(Tags.NODE).value(id);
            		writer.name(Tags.ENTRY);
            		onValue(reader);
            	}
            	break;
            
            default:
            	reader.skipValue();
            	Debug.getInstance().debug("JSONParser", "Error skipping while parsing @array");
    			throw new RuntimeException();
			}
		}
		writer.endObject();
		reader.endObject();
		
		if (unreadValue) {
			Debug.getInstance().debug("JSONParser", "Parsing array with null version");
			throw new RuntimeException();
		}
		
		return Repository.getOrCreateClassVersion(Tags.ARRAY);
	}
	
	private int onString(final JsonReader reader) throws Exception {
		boolean unreadValue = true;
		reader.beginObject();
		writer.beginObject();
		while (reader.hasNext()) {
			final JsonToken token = reader.peek();
			switch (token) {          
            case NAME:
            	final String name = reader.nextName();
            	if (name.equals(Tags.ID)) {
            		reader.nextInt();
            	} else if (name.equals(Tags.HASH)) {
            		unreadValue = false;
            		writer.name(Tags.HASH).value(reader.nextString());
            	}
            	break;
            default:
            	reader.skipValue();
            	Debug.getInstance().debug("JSONParser", "Error skipping while parsing @string");
    			throw new RuntimeException();
			}
		}
		writer.endObject();
		reader.endObject();

		if (unreadValue) {
			Debug.getInstance().debug("JSONParser", "Parsing string with null hash");
			throw new RuntimeException();
		}
		
		return Repository.getOrCreateClassVersion(Tags.STRING);
	}
	
	private int onFile(final JsonReader reader) throws Exception {
		boolean unreadValue = true;
		reader.beginObject();
		while (reader.hasNext()) {
			final JsonToken token = reader.peek();
			switch (token) {
			case NAME:
            	final String name = reader.nextName();
            	if (name.equals(Tags.STRING)) {
            		unreadValue = false;
            		onString(reader);
            	}
            	break;
            default:
            	reader.skipValue();
            	Debug.getInstance().debug("JSONParser", "Error skipping while parsing @file");
    			throw new RuntimeException();
			}
		}
		reader.endObject();
		
		if (unreadValue) {
			Debug.getInstance().debug("JSONParser", "Parsing file with null version");
			throw new RuntimeException();
		}
		
		return Repository.getOrCreateClassVersion(Tags.FILE);
	}
	
	private int onPattern(final JsonReader reader) throws Exception {
		boolean unreadValue = true;
		reader.beginObject();
		while (reader.hasNext()) {
			final JsonToken token = reader.peek();
			switch (token) {
			case NAME:
            	final String name = reader.nextName();
            	if (name.equals(Tags.STRING)) {
            		unreadValue = false;
            		onString(reader);
            	}
            	break;
            default:
            	reader.skipValue();
            	Debug.getInstance().debug("JSONParser", "Error skipping while parsing @pattern");
    			throw new RuntimeException();
			}
		}
		reader.endObject();
		
		if (unreadValue) {
			Debug.getInstance().debug("JSONParser", "Parsing patterns with null version");
			throw new RuntimeException();
		}
		
		return Repository.getOrCreateClassVersion(Tags.PATTERN);
	}
	
	private int onCollection(final JsonReader reader) throws Exception {
		int id = Integer.MIN_VALUE;
		boolean unreadValue = true;
		reader.beginObject();
		writer.beginObject();
		while (reader.hasNext()) {
			final JsonToken token = reader.peek();
			switch (token) {
			case NAME:
            	final String name = reader.nextName();
            	if (name.equals(Tags.ID)) {
            		id = nodeMapping.getOrCreateMapping(reader.nextInt());
            		unreadValue = false;
            	} else if (name.equals(Tags.TYPE)) {
            		reader.nextString();
            	} else if (name.equals(Tags.ENTRY)) {
            		assert id != Integer.MIN_VALUE;
            		writer.name(Tags.NODE).value(id);
            		writer.name(Tags.ENTRY);
            		onValue(reader);
            	}
            	break;
            
            default:
            	reader.skipValue();
            	Debug.getInstance().debug("JSONParser", "Error skipping while parsing @collection");
    			throw new RuntimeException();
			}
		}
		writer.endObject();
		reader.endObject();
		
		if (unreadValue) {
			Debug.getInstance().debug("JSONParser", "Parsing collection with null version");
			throw new RuntimeException();
		}
		
		return Repository.getOrCreateClassVersion(Tags.COLLECTION);
	}
	
	private int onMap(final JsonReader reader) throws Exception {

		int id = Integer.MIN_VALUE;
		boolean unreadValue = true;
		
		reader.beginObject();
		writer.beginObject();
		while (reader.hasNext()) {
			final JsonToken token = reader.peek();
			switch (token) {
			case NAME:	
            	final String name = reader.nextName();
            	if (name.equals(Tags.ID)) {
            		id = nodeMapping.getOrCreateMapping(reader.nextInt());
            		unreadValue = false;
            	} else if (name.equals(Tags.TYPE)) {
            		reader.nextString();
            	} else if (name.equals(Tags.ENTRY)) {
            		assert id != Integer.MIN_VALUE;
            		writer.name(Tags.NODE).value(id);
            		reader.beginObject();
            			writer.name(Tags.ENTRY);
            			writer.beginObject();	
            				writer.name(Tags.MAP_KEY);
            					reader.nextName();
            					onValue(reader);
            				writer.name(Tags.MAP_VALUE);
            					reader.nextName();
            					onValue(reader);
            			writer.endObject();
            		reader.endObject();
            	}
            	break;
            default:
            	reader.skipValue();
            	Debug.getInstance().debug("JSONParser", "Error skipping while parsing @map");
    			throw new RuntimeException();
			}
		}
		writer.endObject();
		reader.endObject();
		
		if (unreadValue) {
			Debug.getInstance().debug("JSONParser", "Parsing map with null version");
			throw new RuntimeException();
		}
		
		return Repository.getOrCreateClassVersion(Tags.MAP);
		
	}
	
	private int onLink(final JsonReader reader) throws Exception {
		boolean unreadValue = true;
		reader.beginObject();
		writer.beginObject();
		while (reader.hasNext()) {
			final JsonToken token = reader.peek();
			switch (token) {
			case NAME:
            	final String arrayName = reader.nextName();
            	if (arrayName.equals(Tags.ID)) {
            		unreadValue = false;
            		int id = nodeMapping.getOrCreateMapping(reader.nextInt());
            		writer.name(Tags.LINK).value(id);
            	}
            	break;
            default:
            	reader.skipValue();
            	Debug.getInstance().debug("JSONParser", "Error skipping while parsing @link");
    			throw new RuntimeException();
			}
		}
		reader.endObject();
		writer.endObject();

		if (unreadValue) {
			Debug.getInstance().debug("JSONParser", "Parsing link with null version");
			throw new RuntimeException();
		}
		
		return Repository.getOrCreateClassVersion(Tags.LINK);
	}
	
	private int onSpecial(final JsonReader reader) throws Exception {
		
		boolean unreadValue = true;
		
		reader.beginObject();
		writer.beginObject();
		while (reader.hasNext()) {
			final JsonToken token = reader.peek();
			switch (token) {          
            case NAME:
            	final String name = reader.nextName();
            	if (name.equals(Tags.ID)) {
            		reader.nextInt();
            	} else if (name.equals(Tags.HASH)) {
            		unreadValue = false;
            		writer.name(Tags.HASH).value(reader.nextString());
            	}
            	break;
            default:
            	reader.skipValue();
            	Debug.getInstance().debug("JSONParser", "Error skipping while parsing @special");
    			throw new RuntimeException();
			}
		}
		writer.endObject();
		reader.endObject();

		if (unreadValue) {
			Debug.getInstance().debug("JSONParser", "Parsing special with null hash");
			throw new RuntimeException();
		}
		
		return Repository.getOrCreateClassVersion(Tags.SPECIAL);
	}
	
	private int onObject(final JsonReader reader) throws Exception {
		
		int id = Integer.MIN_VALUE;
		int clazz = Version.INITIAL_MAJOR_VERSION;
		
		reader.beginObject();
		writer.beginObject();
		while (reader.hasNext()) {
			final JsonToken token = reader.peek();
			switch (token) {
			case NAME:
            	final String name = reader.nextName();
            	if (name.equals(Tags.ID)) {
            		id = nodeMapping.getOrCreateMapping(reader.nextInt());
            	} else if (name.equals(Tags.TYPE)) {
            		clazz = Repository.getOrCreateClassVersion(reader.nextString());
            	} else {
            		int field = getOrCreateFieldIdentifier(name);
            		writer.name(Tags.NODE).value(id);
            		writer.name(Integer.toString(field));
            		onValue(reader);
            	}
            	break;
			
            default:
            	reader.skipValue();
            	Debug.getInstance().debug("JSONParser", "Error skipping while parsing @object");
    			throw new RuntimeException();
			}
			
		}
		writer.endObject();
		reader.endObject();
		
		return clazz;
		
	}
	
	private int onNull(final JsonReader reader) throws Exception {
		reader.nextNull();
		writer.beginObject();
		writer.name(Tags.NULL).nullValue();
		writer.endObject();
		return Version.NULL_MAJOR_VERSION;
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
            		clazz = onNull(reader);
				} else if (name.equals(Tags.LINK)) {
            		clazz = onLink(reader);
            	} else if (name.equals(Tags.ARRAY)) {
            		clazz = onArray(reader); 
				} else if (name.equals(Tags.COLLECTION)) {
					clazz = onCollection(reader);
            	} else if (name.equals(Tags.MAP)) {
            		clazz = onMap(reader);
				} else if (name.equals(Tags.SPECIAL)) {
					clazz = onSpecial(reader);
				} else if (name.equals(Tags.STRING)) {
					clazz = onString(reader);
				} else if (name.equals(Tags.PATTERN)) {
					clazz = onPattern(reader);
				} else if (name.equals(Tags.FILE)) {
					clazz = onFile(reader);
				} else if (name.equals(Tags.PRIMITIVE)) {
					clazz = onPrimitive(reader);
				} else if (name.equals(Tags.HASH)) {
					clazz = onSpecial(reader);
				} else if (name.equals(Tags.OBJECT)) {
					clazz = onObject(reader);
				}
            	break;
            default:
            	reader.skipValue();
            	Debug.getInstance().debug("JSONParser", "Error skipping while parsing @value");
    			throw new RuntimeException();
			}
		}
		reader.endObject();
		return clazz;
	}
	
	@Override
	protected String onCreateVersion(final String buffer) throws Exception {
		final HashCode hc = Hashing.murmur3_128().newHasher().putString(buffer).hash();
		return hc.toString();
	}

	@Override
	protected void onResetState() {
		writer = new JsonWriter(writerBuffer);
		nodeMapping = new Mapping();
	}

}
