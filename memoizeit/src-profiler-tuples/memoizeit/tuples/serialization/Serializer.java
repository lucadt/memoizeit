package memoizeit.tuples.serialization;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import memoizeit.asm.BaseOptions;
import memoizeit.asm.TargetAccesses;
import memoizeit.asm.profiler.AbstractFileProfiler;
import memoizeit.tuples.instr.Options;

import com.google.gson.stream.JsonWriter;

public final class Serializer extends AbstractFileProfiler implements SerializerConstants {
		
	private static final int BUFFER_SIZE = 1024*1024;
	private JsonWriter writer;
	private AbstractSerializerHelper helper;
	
	public Serializer(String filePath, String fileName) {
		super(filePath, fileName);
		this.writer = null;
	}
	
	private void createCompressedStream() {
		final Deflater def = new Deflater();
	    def.setLevel(Deflater.BEST_SPEED);
	    try {
	    	final FileOutputStream fos = new FileOutputStream(getFileHandler());
	    	final DeflaterOutputStream dos = new DeflaterOutputStream(fos, def, BUFFER_SIZE);
			writer = new JsonWriter(new OutputStreamWriter(dos));
		} catch (final FileNotFoundException ex) {
			ex.printStackTrace();
		}
	}
	
	private void createStream() {
		try {
			final FileWriter fw = new FileWriter(getFileHandler());
			writer = new JsonWriter(new BufferedWriter(fw, BUFFER_SIZE));
		} catch (final IOException ex) {
			ex.printStackTrace();
		}
	}
	
	private void onSetup() {
		if (BaseOptions.useCompressedStream()) {
			createCompressedStream();
		} else {
			createStream();
		}
		if (Options.useCustomHashCode()) {
			helper = new HasherSerializerHelper();
		} else {
			helper = new JSONSerializerHelper(writer);
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
		try {
			onSetup();
			writer.beginArray();
		} catch (final Exception ex) {
			ex.printStackTrace();			
		}
	}
	
	@Override
	public void onShutdown() {
		try {
			writer.endArray();
			writer.flush();
			writer.close();
		} catch (final Exception ex) {
			ex.printStackTrace();			
		}
		super.onShutdown();
	}
	
	private void dumpHeader(byte type, int index, long call, int arg) throws IOException {
		helper.onReset();
		writer.name(Tags.INDEX).value(index);
		writer.name(Tags.CALL).value(call);
		writer.name(Tags.TYPE).value(type);
		if (type == TYPE_PARAMETER) {
			writer.name(Tags.PARAMETER).value(arg);	
		}
		writer.name(Tags.VALUE);				
	}
	
	private String dumpPayload(final String type) throws IOException {
		if (Options.useCustomHashCode()) {
			final String hashCode = ((HasherSerializerHelper) helper).getHash();
			writer.beginObject();
			writer.name(Tags.HASH_CODE);
			writer.beginObject();
			writer.name(Tags.TYPE).value(type);
			writer.name(Tags.HASH).value(hashCode);
			writer.endObject();
			writer.endObject();
			return hashCode;
		}
		return null;
	}
	
	public void dumpTarget(byte type, int index, long call, int arg, final Object anObject) {	
		try {
			final Class<? extends Object> aClass = anObject.getClass();
			if (TargetAccesses.getInstance().isLoaded()) {
				if (TargetAccesses.getInstance().isAccessingFields(index, aClass.getName())) {
					writer.beginObject();
					dumpHeader(type, index, call, arg);
					helper.onTarget(anObject, index);
					dumpPayload(aClass.getName());
					writer.endObject();
				}
			} else {
				writer.beginObject();
				dumpHeader(type, index, call, arg);
				helper.onTarget(anObject, index);
				dumpPayload(aClass.getName());
				writer.endObject();
			}
		} catch (final Exception ex) {
			ex.printStackTrace();			
		}
	}
	

	public void dumpNull(byte type, int index, long call, int arg) {	
		try {
			writer.beginObject();
			dumpHeader(type, index, call, arg);
			helper.onNull();
			dumpPayload(Tags.NULL);
			writer.endObject();				
		} catch (final Exception ex) {
			ex.printStackTrace();			
		}
	}
		
	public void dumpObject(byte type, int index, long call, int arg, final Object anObject) {
		try {
			final Class<? extends Object> aClass = anObject.getClass();
			writer.beginObject();
			dumpHeader(type, index, call, arg);
			helper.onObject(anObject);
			dumpPayload(aClass.getName());
			writer.endObject();
		} catch (final Exception ex) {
			ex.printStackTrace();			
		}
	}
	
}
