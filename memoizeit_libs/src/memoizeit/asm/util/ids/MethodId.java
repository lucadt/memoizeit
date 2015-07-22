package memoizeit.asm.util.ids;


import org.objectweb.asm.Type;

public final class MethodId extends AbstractIdManager {
	
	private static final MethodId INSTANCE = new MethodId();
	
	public static synchronized MethodId getInstance() {
		return INSTANCE;
	}
	
	public static final String getMethodReturn(final String name) {
		return name.split("\\:")[0];
	}

	public static final String getClassName(final String name) {
		return name.split("\\:")[1];
	}
	
	public static final String getMethodNameWithParameters(final String name) {
		return name.split("\\:")[2];
	}
	
	public static final String getSignatureNoReturn(final String owner, final String name, final String descriptor, char separator) {
		final Type[] arguments = Type.getArgumentTypes(descriptor);
		String returnValue = owner.replace('/', '.') + separator + name + '(';
		if (arguments.length > 0) {
			returnValue += arguments[0].getClassName();
			if (arguments.length > 1) {
				for (int i=1; i < arguments.length; i++) {
					returnValue += ',' + arguments[i].getClassName();
				}
			}
		}
		return returnValue + ')';
	}
	
	public final String getSignature(final String owner, final String name, final String descriptor) {
		final Type[] arguments = Type.getArgumentTypes(descriptor);
		final Type returns = Type.getReturnType(descriptor);
		String returnValue = returns.getClassName() + ':' + owner.replace('/', '.') + ':' + name + '(';
		if (arguments.length > 0) {
			returnValue += arguments[0].getClassName();
			if (arguments.length > 1) {
				for (int i=1; i < arguments.length; i++) {
					returnValue += ',' + arguments[i].getClassName();
				}
			}	
		}
		return returnValue + ')';
	}
	
}
