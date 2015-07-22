package memoizeit.tuples.instr.adapter;

import memoizeit.asm.util.AsmHelper;
import memoizeit.tuples.serialization.Types;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

public final class FlagAdderAdapter extends ClassVisitor {

	public FlagAdderAdapter(final ClassVisitor cv) {
		super(Opcodes.ASM4, cv);
	}
	
    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
    	if (AsmHelper.isInterface(access)) {
    		cv.visit(version, access, name, signature, superName, interfaces);
    	} else {
        	if (superName.equals(Types.OBJECT.getInternalName())) {
        		cv.visit(version, access, name, signature, Types.OBJECT_TAG.getInternalName(), interfaces);
        	} else {
        		cv.visit(version, access, name, signature, superName, interfaces); 
        	}
    	}
    }

}
