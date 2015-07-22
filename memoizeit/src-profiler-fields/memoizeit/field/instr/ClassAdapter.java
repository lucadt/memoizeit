package memoizeit.field.instr;

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
    public MethodVisitor visitMethod(
        final int access,
        final String name,
        final String desc,
        final String signature,
        final String[] exceptions)
    {
    	final MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
    	return mv == null ? null : new TraceAdapter(mv, cn.name, name, access, desc);
    }
	
}
