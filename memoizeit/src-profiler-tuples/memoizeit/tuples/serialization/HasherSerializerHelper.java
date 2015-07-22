package memoizeit.tuples.serialization;

import memoizeit.asm.util.ids.Mapping;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

public final class HasherSerializerHelper extends RecursiveAbstractSerializerHelper {
	
	private final HashFunction hf = Hashing.murmur3_128();
	private Hasher hasher;
	
	private final Mapping nodeMapping = new Mapping();
	
	public String getHash() {
		return hasher.hash().toString();
	}
	
	@Override
	public void onReset() {
		hasher = hf.newHasher();
		nodeMapping.reset();
		super.onReset();
	}
		
	@Override
	public void onObjectType(final String aString) throws Exception {
		if (aString != null) hasher.putString(aString);
	}

	@Override
	public void onObjectStart(final Object anObject) throws Exception {
		final Class<? extends Object> aClass = anObject.getClass();
		int id = nodeMapping.getOrCreateMapping(getIdentifier(anObject));
        hasher.putString(id+':'+aClass.getName()+'{');
	}

	@Override
	public void onObjectField(final String aString) throws Exception {
		hasher.putString(aString+':');
	}

	@Override
	public void onObjectTypeEnd() throws Exception {  }

	@Override
	public void onObjectEnd() throws Exception { 
		hasher.putChar('}');		
	}
	
	@Override
	public void onLink(final Object anObject) throws Exception {
		hasher.putInt(nodeMapping.getOrCreateMapping(getIdentifier(anObject)));
	}

	@Override
	public void onHash(final Object anObject) throws Exception {
		hasher.putString(anObject.getClass().getName());
	}

	@Override
	public void onSpecial(final Object anObject) throws Exception {
		onHash(anObject);
	}

	@Override
	public void onString(final String aString) throws Exception {
		hasher.putString(aString);
	}

	@Override
	public void onNull() throws Exception {
		hasher.putString(Tags.NULL);
	}

	@Override
	public void onNumber(final Number aNumber) throws Exception {
		hasher.putString(aNumber.toString());
	}

	@Override
	public void onBoolean(boolean aBoolean) throws Exception {
		hasher.putBoolean(aBoolean);
	}

	@Override
	public void onByte(byte aByte) throws Exception {
		hasher.putByte(aByte);
	}

	@Override
	public void onInteger(int anInt) throws Exception {
		hasher.putInt(anInt);
	}

	@Override
	public void onLong(long aLong) throws Exception {
		hasher.putLong(aLong);
	}

	@Override
	public void onShort(short aShort) throws Exception {
		hasher.putShort(aShort);
	}

	@Override
	public void onChar(char aChar) throws Exception {
		hasher.putChar(aChar);
	}

	@Override
	public void onFloat(float aFloat) throws Exception {
		hasher.putFloat(aFloat);
	}

	@Override
	public void onDouble(double aDouble) throws Exception {
		hasher.putDouble(aDouble);
	}
	
}
