package memoizeit.tuples.instr.adapter;

import memoizeit.asm.util.AsmHelper;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

public final class ClassNodeAdapter extends ClassVisitor {

	private final ClassVisitor next;
	
	public ClassNodeAdapter(final ClassVisitor cv) {
		super(Opcodes.ASM4, new ClassNode());
		this.next = cv;
	}
	
	@Override
	public void visitEnd() {
		final ClassNode cn = (ClassNode) cv;
		if (AsmHelper.isInterface(cn.access)) {
			cn.accept(this.next);
		} else {
			cn.accept(new ClassAdapter(this.next, (ClassNode) cv));			
		}
	}

}
