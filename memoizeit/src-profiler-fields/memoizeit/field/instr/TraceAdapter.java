package memoizeit.field.instr;

import memoizeit.asm.util.AsmHelper;
import memoizeit.asm.util.ids.FieldId;
import memoizeit.asm.util.ids.MethodId;
import memoizeit.field.profiler.Profiler;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.Method;

public final class TraceAdapter extends AdviceAdapter {
	
	private final static Type PROFILER = Type.getType(Profiler.class);
	
	private final static Method ON_METHOD_ENTRY = Method.getMethod("void onMethodEntry(int,java.lang.Object)");
	private final static Method ON_METHOD_EXIT = Method.getMethod("void onMethodExit(int,java.lang.Object)");
	private final static Method ON_METHOD_EXCEPTION = Method.getMethod("void onMethodException(int,java.lang.Object)");
	private final static Method ON_FIELD_READ = Method.getMethod("void onFieldRead(java.lang.Object,int,int)");
	private final static Method ON_FIELD_STATIC_READ = Method.getMethod("void onFieldStaticRead(int,int)");
	private final static Method ON_FIELD_WRITE = Method.getMethod("void onFieldWrite(java.lang.Object,int,int)");
	private final static Method ON_FIELD_STATIC_WRITE = Method.getMethod("void onFieldStaticWrite(int,int)");

	private final int index;

	protected TraceAdapter(final MethodVisitor mv, final String owner, final String name, int access, final String desc) {
		super(Opcodes.ASM4, mv, access, name, desc);
		this.index = MethodId.getInstance().getIndex(owner, name, desc);
	}
		
	private void myLoadThis() {
		if (AsmHelper.isStatic(methodAccess) || 
			AsmHelper.isStaticConstructur(methodDesc) || 
			AsmHelper.isConstructur(methodDesc)) {
			mv.visitInsn(ACONST_NULL);
		} else {
			loadThis();			
		}
	}
	
	@Override
	public void visitFieldInsn(int opcode, String owner, String name, String desc) {
		
		if (AsmHelper.isEnclosingClassField(name)) {
			super.visitFieldInsn(opcode, owner, name, desc);
			return;
		}
		
		int fieldIndex = FieldId.getInstance().getIndex(owner, name, desc);
		
		if (opcode == Opcodes.GETFIELD) {
			
			dup();
			push(index);
			push(fieldIndex);
			invokeStatic(PROFILER, ON_FIELD_READ);
			
		} else if (opcode == Opcodes.PUTFIELD) {

			final Type fieldType = Type.getType(desc);
			final Type ownerType = Type.getType("L"+owner+";");
			
			swap(ownerType, fieldType);
			dup();
			push(index);
			push(fieldIndex);
			invokeStatic(PROFILER, ON_FIELD_WRITE);
			swap(fieldType, ownerType);
			
		} else if (opcode == Opcodes.GETSTATIC) {
			
			push(index);
			push(fieldIndex);
			invokeStatic(PROFILER, ON_FIELD_STATIC_READ);
			
		} else if (opcode == Opcodes.PUTSTATIC) {
			
			push(index);
			push(fieldIndex);
			invokeStatic(PROFILER, ON_FIELD_STATIC_WRITE);
			
		}
		
		super.visitFieldInsn(opcode, owner, name, desc);
		
	}
	
	@Override
	protected void onMethodEnter() {
		push(index);
		myLoadThis();
		invokeStatic(PROFILER, ON_METHOD_ENTRY);
		super.onMethodEnter();
	}
	
	@Override
	protected void onMethodExit(int opcode) {
		if (opcode != ATHROW) {
			onMethodFinally(opcode);
		}
	}
	
	private void onMethodFinally(int opcode) {
		push(index);
		myLoadThis();
		if (opcode != ATHROW) {
			invokeStatic(PROFILER, ON_METHOD_EXIT);
		} else {
			invokeStatic(PROFILER, ON_METHOD_EXCEPTION);
		}
	}
	
}
