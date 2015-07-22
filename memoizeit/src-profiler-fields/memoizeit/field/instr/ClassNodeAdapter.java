package memoizeit.field.instr;

import memoizeit.asm.util.ids.ClassId;
import memoizeit.field.instr.ClassAdapter;

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
		ClassId.getInstance().getIndex(cn.name, null, null);
		cn.accept(new ClassAdapter(this.next, (ClassNode) cv));			
	}

}