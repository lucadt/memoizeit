package memoizeit.asm.util.ids;

import org.objectweb.asm.Type;

public final class ClassId extends AbstractIdManager {
	
	private static final ClassId INSTANCE = new ClassId();
	
	public static synchronized ClassId getInstance() {
		return INSTANCE;
	}

	@Override
	public String getSignature(final String owner, final String name, final String descriptor) {
		final Type classType = Type.getType('L'+owner+';');
		return classType.getClassName();
	}

}
