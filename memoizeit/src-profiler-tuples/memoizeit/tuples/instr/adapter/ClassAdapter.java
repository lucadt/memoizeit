package memoizeit.tuples.instr.adapter;


import java.util.Arrays;

import memoizeit.tuples.instr.TraverseAdder;
import memoizeit.tuples.serialization.Types;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

public final class ClassAdapter extends ClassVisitor {
	
	private final ClassNode cn;
	
    public ClassAdapter(final ClassVisitor cv, final ClassNode cn) {
        super(Opcodes.ASM4, cv);
        this.cn = cn;
    }
    
    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
    	final String[] nInterfaces = Arrays.copyOf(interfaces, interfaces.length + 1);
    	nInterfaces[interfaces.length] = Types.TRAVERSER.getInternalName();    			    			
    	cv.visit(version, access, name, signature, superName, nInterfaces);
    }
    
    @Override
    public MethodVisitor visitMethod(
        final int access,
        final String name,
        final String desc,
        final String signature,
        final String[] exceptions)
    {
    	final MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
    	return mv == null ? null : new MethodFilterAdapter(access, cn.name, name, desc, signature, exceptions, mv);    		
    }
    
    @Override
    public void visitEnd() {
    	new TraverseAdder(cn, cv).generate();    				
		cv.visitEnd();
    }
    	
}
