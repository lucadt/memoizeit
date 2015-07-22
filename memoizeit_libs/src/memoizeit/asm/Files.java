package memoizeit.asm;

import java.io.FileReader;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

public final class Files {
		
	public static String TEMPORARY_DIR = "tmp_profiler";
	public static String BYTECODE_DIR = "bytecode";
	public static String TUPLES_DIR = "tuples";
	public static String TIME_DIR = "time";
	public static String FIELDS_DIR = "fields";
	public static String DATA_DIR = "data";

	public static String CLASSES_FILE = "classes.txt";
	public static String METHODS_FILE = "methods.txt";
	public static String FIELDS_FILE = "fields.txt";
	public static String WHITE_LIST_FILE = "white_list.txt";
	public static String STATS_FILE = "stats.txt";
	public static String DEBUG_FILE = "debug.txt";

	public static String TIME_TRACE = "time.bin";
	public static String TIME_OUTPUT = "time.txt";
	public static String TIME_TOTAL = "total.txt";
	
	public static String TUPLES_TRACE = "versions.bin";
	public static String TUPLES_PARAMETERS = "parameters.json";
	public static String TUPLES_TARGETS = "targets.json";
	public static String TUPLES_RESULTS = "results.json";
	public static String TUPLES_OUTPUT = "versions.txt";
	public static String TUPLES_MAX_DEPTH = "max_depth.txt";
	public static String TUPLES_LOG_DEPTH = "log_depth.txt";

	public static String FIELDS_TRACE = "fields.bin";
	public static String FIELDS_OUTPUT = "accesses.bin";
	
	private static final void onReadDirectories(final JsonReader reader) throws Exception {
		reader.beginObject();
		while (reader.hasNext()) {
			final JsonToken token = reader.peek();
			switch (token) {          
            case NAME:
                final String name = reader.nextName();                                
                if (name.equals("temporary")) {
                	TEMPORARY_DIR = reader.nextString();
                } else if (name.equals("bytecode")) {
                	BYTECODE_DIR = reader.nextString();
                } else if (name.equals("tuples")) {
                	TUPLES_DIR = reader.nextString();
                } else if (name.equals("fields")) {
                	FIELDS_DIR = reader.nextString();
                } else if (name.equals("time")) {
                	TIME_DIR = reader.nextString();
                } else if (name.equals("data")) {
                	DATA_DIR = reader.nextString();
                }
                break;
            default:
            	reader.skipValue();
            	break;
            }				
		}
		reader.endObject();
	}
	
	private static final void onReadFiles(final JsonReader reader) throws Exception {
		reader.beginObject();
		while (reader.hasNext()) {
			final JsonToken token = reader.peek();
			switch (token) {          
            case NAME:
                final String name = reader.nextName();                                
                if (name.equals("classes")) {
                	CLASSES_FILE = reader.nextString();
                } else if (name.equals("methods")) {
                	METHODS_FILE = reader.nextString();
                } else if (name.equals("fields")) {
                	FIELDS_FILE = reader.nextString();
                } else if (name.equals("debug")) {
                	DEBUG_FILE = reader.nextString();
                } else if (name.equals("statistics")) {
                	STATS_FILE = reader.nextString();
                } else if (name.equals("white_list")) {
                	WHITE_LIST_FILE = reader.nextString();
                }
                break;
            default:
            	reader.skipValue();
            	break;
            }				
		}
		reader.endObject();
	}
	
	private static final void onReadTuples(final JsonReader reader) throws Exception {
		reader.beginObject();
		while (reader.hasNext()) {
			final JsonToken token = reader.peek();
			switch (token) {          
            case NAME:
                final String name = reader.nextName();                                
                if (name.equals("targets")) {
                	TUPLES_TARGETS = reader.nextString();
                } else if (name.equals("parameters")) {
                	TUPLES_PARAMETERS = reader.nextString();
                } else if (name.equals("results")) {
                	TUPLES_RESULTS = reader.nextString();
                } else if (name.equals("max_depth")) {
                	TUPLES_MAX_DEPTH = reader.nextString();
                } else if (name.equals("trace")) {
                	TUPLES_TRACE = reader.nextString();
                } else if (name.equals("output")) {
                	TUPLES_OUTPUT = reader.nextString();
                }
                break;
            default:
            	reader.skipValue();
            	break;
            }				
		}
		reader.endObject();
	}
	
	private static final void onReadFields(final JsonReader reader) throws Exception {
		reader.beginObject();
		while (reader.hasNext()) {
			final JsonToken token = reader.peek();
			switch (token) {          
            case NAME:
                final String name = reader.nextName();                                
                if (name.equals("trace")) {
                	FIELDS_TRACE = reader.nextString();
                } else if (name.equals("output")) {
                	FIELDS_OUTPUT = reader.nextString();
                }
                break;
            default:
            	reader.skipValue();
            	break;
            }				
		}
		reader.endObject();
	}
	
	private static final void onReadTime(final JsonReader reader) throws Exception {
		reader.beginObject();
		while (reader.hasNext()) {
			final JsonToken token = reader.peek();
			switch (token) {          
            case NAME:
                final String name = reader.nextName();                                
                if (name.equals("trace")) {
                	TIME_TRACE = reader.nextString();
                } else if (name.equals("output")) {
                	TIME_OUTPUT = reader.nextString();
                } else if (name.equals("total")) {
                	TIME_TOTAL = reader.nextString();
                }
                break;
            default:
            	reader.skipValue();
            	break;
            }				
		}
		reader.endObject();
	}
	
	static {
		try {
			final FileReader fr = new FileReader(BaseOptions.getOptionsFile());
			final JsonReader reader = new JsonReader(fr);
			reader.beginObject();
			while (reader.hasNext()) {
				final JsonToken token = reader.peek();
				switch (token) {          
	            case NAME:
	                final String name = reader.nextName();                                
	                if (name.equals("dirs")) {
	                	onReadDirectories(reader);
	                } else if (name.equals("files")) {
	                	onReadFiles(reader);
	                } else if (name.equals("tuples")) {
	                	onReadTuples(reader);
	                } else if (name.equals("fields")) {
	                	onReadFields(reader);
	                } else if (name.equals("time")) {
	                	onReadTime(reader);
	                }
	                break;
	            default:
	            	reader.skipValue();
	            	break;
	            }
			}
			reader.endObject();
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}
}
