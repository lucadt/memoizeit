package memoizeit.asm.util.ids;

import org.objectweb.asm.Type;

public class FieldId extends AbstractIdManager {
	
	private static final FieldId INSTANCE = new FieldId();
	
	public static synchronized FieldId getInstance() {
		return INSTANCE;
	}
	
	public final String getSignature(final String owner, final String name, final String descriptor) {
		final Type fieldType = Type.getType(descriptor);
		return owner.replace('/', '.') + ':' + name + ':' + fieldType.getClassName();
	}
	
	public static final String getFieldClass(final String signature) {
		return signature.split("\\:")[0];
	}
	
	public static final String getFieldName(final String signature) {
		return signature.split("\\:")[1];
	}
	
	public static final String getFieldType(final String signature) {
		return signature.split("\\:")[2];
	}

}
