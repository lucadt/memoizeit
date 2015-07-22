package memoizeit.field.instr;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import memoizeit.asm.Debug;
import memoizeit.asm.util.Util;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

public class Instrument implements ClassFileTransformer {
			
	@Override
	public byte[] transform(ClassLoader loader,String className,Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer)
			throws IllegalClassFormatException {
	
		if (Util.isNotToProfile(className)) {
			return null;
		}
		
		try {
			
			final ClassReader classReader = new ClassReader(classfileBuffer);
			final ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			final ClassVisitor profileAdapter = new ClassNodeAdapter(classWriter);
			
			classReader.accept(profileAdapter, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
						
			return classWriter.toByteArray();
			
		} catch (final Exception ex) {
			Debug.getInstance().debug("Field.Instrument", "Error instrumenting class " + className + ".");
			return null;
		}
		
	}
		
}
