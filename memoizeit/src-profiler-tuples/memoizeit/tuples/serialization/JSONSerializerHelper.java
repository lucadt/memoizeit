package memoizeit.tuples.serialization;

import com.google.gson.stream.JsonWriter;

public final class JSONSerializerHelper extends RecursiveAbstractSerializerHelper {

	private final JsonWriter writer;

	public JSONSerializerHelper(final JsonWriter writer) {
		this.writer = writer;
	}
		
	@Override
	public void onObjectType(final String aString) throws Exception {
        writer.beginObject();
        if (aString != null) writer.name(aString);
	}

	@Override
	public void onObjectStart(final Object anObject) throws Exception {
		final Class<? extends Object> aClass = anObject.getClass();
		writer.beginObject();
        writer.name(Tags.ID).value(getIdentifier(anObject));
        writer.name(Tags.TYPE).value(aClass.getName());
	}

	@Override
	public void onObjectField(final String aString) throws Exception {
		writer.name(aString);		
	}

	@Override
	public void onObjectEnd() throws Exception {
    	writer.endObject();
	}
	
	@Override
	public void onObjectTypeEnd() throws Exception {
		writer.endObject();
	}
	
	@Override
	public void onLink(final Object anObject) throws Exception {
		onObjectType(Tags.LINK);
		writer.beginObject();
		writer.name(Tags.ID).value(getIdentifier(anObject));
		writer.endObject();
		onObjectTypeEnd();
	}

	@Override
	public void onHash(final Object anObject) throws Exception {		
		onObjectType(Tags.HASH);
		writer.beginObject();
		writer.name(Tags.ID).value(getIdentifier(anObject));
		writer.name(Tags.HASH).value(anObject.getClass().getName());
		writer.endObject();
		onObjectTypeEnd();
	}

	@Override
	public void onSpecial(final Object anObject) throws Exception {
		onObjectType(Tags.SPECIAL);
		writer.beginObject();
		writer.name(Tags.ID).value(getIdentifier(anObject));
		writer.name(Tags.HASH).value(anObject.getClass().getName());
		writer.endObject();
		onObjectTypeEnd();
	}

	@Override
	public void onString(final String aString) throws Exception {
		onObjectType(Tags.STRING);
		writer.beginObject();
		writer.name(Tags.ID).value(getIdentifier(aString));
		writer.name(Tags.HASH).value(aString.hashCode());
		writer.endObject();
		onObjectTypeEnd();
	}

	@Override
	public void onNull() throws Exception {
		writer.beginObject();
		writer.name(Tags.NULL).nullValue();
		writer.endObject();		
	}

	@Override
	public void onNumber(final Number aNumber) throws Exception {
		onObjectType(Tags.PRIMITIVE);
		writer.beginObject();
		writer.name(Tags.DATA).value(aNumber);
		writer.endObject();
		onObjectTypeEnd();
	}

	@Override
	public void onBoolean(boolean aBoolean) throws Exception {
		onObjectType(Tags.PRIMITIVE);
		writer.beginObject();
		writer.name(Tags.DATA).value(aBoolean);
		writer.endObject();
		onObjectTypeEnd();
	}

	@Override
	public void onByte(byte aByte) throws Exception {
		onObjectType(Tags.PRIMITIVE);
		writer.beginObject();
		writer.name(Tags.DATA).value(aByte);
		writer.endObject();
		onObjectTypeEnd();
	}

	@Override
	public void onInteger(int anInt) throws Exception {
		onObjectType(Tags.PRIMITIVE);
		writer.beginObject();
		writer.name(Tags.DATA).value(anInt);
		writer.endObject();
		onObjectTypeEnd();
	}

	@Override
	public void onLong(long aLong) throws Exception {
		onObjectType(Tags.PRIMITIVE);
		writer.beginObject();
		writer.name(Tags.DATA).value(aLong);
		writer.endObject();
		onObjectTypeEnd();
	}

	@Override
	public void onShort(short aShort) throws Exception {
		onObjectType(Tags.PRIMITIVE);
		writer.beginObject();
		writer.name(Tags.DATA).value(aShort);
		writer.endObject();
		onObjectTypeEnd();
	}

	@Override
	public void onChar(char aChar) throws Exception {
		onObjectType(Tags.PRIMITIVE);
		writer.beginObject();
		writer.name(Tags.DATA).value(aChar);
		writer.endObject();
		onObjectTypeEnd();
	}

	@Override
	public void onFloat(float aFloat) throws Exception {
		onObjectType(Tags.PRIMITIVE);
		writer.beginObject();
		if (Float.isInfinite(aFloat)) {
			writer.name(Tags.DATA).value(Float.MAX_VALUE);						
		} else if (Float.isNaN(aFloat)) {
			writer.name(Tags.DATA).value(Float.MIN_VALUE);							
		} else {
			writer.name(Tags.DATA).value(aFloat);			
		}
		writer.endObject();
		onObjectTypeEnd();
	}

	@Override
	public void onDouble(double aDouble) throws Exception {
		onObjectType(Tags.PRIMITIVE);
		writer.beginObject();		
		if (Double.isInfinite(aDouble)) {
			writer.name(Tags.DATA).value(Double.MAX_VALUE);						
		} else if (Double.isNaN(aDouble)) {
			writer.name(Tags.DATA).value(Double.MIN_VALUE);							
		} else {
			writer.name(Tags.DATA).value(aDouble);			
		}
		writer.endObject();
		onObjectTypeEnd();
	}

}
