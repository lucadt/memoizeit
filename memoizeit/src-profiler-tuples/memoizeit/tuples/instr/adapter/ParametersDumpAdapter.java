package memoizeit.tuples.instr.adapter;

import memoizeit.asm.Debug;
import memoizeit.asm.TargetAccesses;
import memoizeit.asm.util.AsmHelper;
import memoizeit.asm.util.ids.MethodId;
import memoizeit.tuples.serialization.Methods;
import memoizeit.tuples.serialization.SerializerConstants;
import memoizeit.tuples.serialization.Types;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

public final class ParametersDumpAdapter extends AdviceAdapter implements Types, Methods, SerializerConstants {
		
	private final int index;
	private int localCallIndex;
	
	protected ParametersDumpAdapter(final MethodVisitor mv, int access, final String owner, final String name, final String desc) {
		super(Opcodes.ASM4, mv, access, name, desc);
		this.index = MethodId.getInstance().getIndex(owner, name, desc);
	}
	
	private void onTracePrimitive(final Type type) {
		switch (type.getSort()) {
		case Type.FLOAT:
			cast(Type.FLOAT_TYPE, Type.DOUBLE_TYPE);
		case Type.DOUBLE:
			invokeStatic(PROFILER, PROFILER_ON_VALUE_DOUBLE);
			break;
		case Type.BOOLEAN:
			mv.visitInsn(I2L);
			invokeStatic(PROFILER, PROFILER_ON_VALUE_LONG);
			break;
		case Type.BYTE:
		case Type.CHAR:
		case Type.INT:
		case Type.SHORT:
			cast(type, Type.LONG_TYPE);
		case Type.LONG:
			invokeStatic(PROFILER, PROFILER_ON_VALUE_LONG);
			break;
		default:
			Debug.getInstance().debug("MemoizeIt.ParametersDumpAdapter", "Profiling wrong type: " + index + " " + type.getInternalName());
			break;
		}
	}
	
	private void onTraceObject() {
		invokeStatic(PROFILER, PROFILER_ON_VALUE_OBJECT);
	}
	
	private void onTraceVoid() {
		invokeStatic(PROFILER, PROFILER_ON_VALUE_VOID);
	}
	
	private void onTraceException() {
		invokeStatic(PROFILER, PROFILER_ON_VALUE_EXCEPTION);
	}

	private void onDumpPushArguments(byte vKind, int vIndex) {
		push(vKind);
		push(vIndex);
		push(index);
		loadLocal(localCallIndex);
	}
	
	private void onDumpObject(byte vKind, int vIndex) {
		onDumpPushArguments(vKind, vIndex);
		invokeStatic(PROFILER, PROFILER_ON_DUMP_OBJECT);
	}
				
	private void onDumpArguments() {
		
		if (!hasInvalidTarget()) {
			if (TargetAccesses.getInstance().isLoaded()) {
				if (TargetAccesses.getInstance().isAccessingFields(index)) {
					myLoadThis();
					onDumpPushArguments(TYPE_TARGET, -1);
					invokeStatic(PROFILER, PROFILER_ON_DUMP_TARGET);
				}
			} else {
				myLoadThis();
				onDumpPushArguments(TYPE_TARGET, -1);
				invokeStatic(PROFILER, PROFILER_ON_DUMP_TARGET);				
			}
		}
		
		final Type[] arguments = Type.getArgumentTypes(super.methodDesc);
					
		push(index);
		loadLocal(localCallIndex);
		push(arguments.length);
		invokeStatic(PROFILER, PROFILER_ON_ARGUMENTS);
		
		for (int i = 0; i < arguments.length; i++) {	
			if (arguments[i].getSort() == Type.ARRAY || arguments[i].getSort() == Type.OBJECT) {
				loadArg(i);
				onTraceObject();
				loadArg(i);
				onDumpObject(TYPE_PARAMETER, i);
			} else {
				loadArg(i);
				onTracePrimitive(arguments[i]);
			}
		}
				
	}

	private boolean hasInvalidTarget() {
		return AsmHelper.isStatic(methodAccess) || AsmHelper.isStaticConstructur(methodDesc) || AsmHelper.isConstructur(methodDesc);
	}
	
	private void myLoadThis() {
		if (hasInvalidTarget()) {
			mv.visitInsn(ACONST_NULL);
		} else {
			loadThis();			
		}
	}

	@Override
	protected void onMethodEnter() {
		localCallIndex = newLocal(Type.LONG_TYPE);
		
		invokeStatic(INDEX_GENERATOR, GENERATOR_NEXT_INDEX);
		storeLocal(localCallIndex);
		
		push(index);
		loadLocal(localCallIndex);	
		myLoadThis();		
		invokeStatic(PROFILER, PROFILER_ON_METHOD_ENTRY);
		
		onDumpArguments();
	}
	
	@Override
	protected void onMethodExit(int opcode) {		
		if (opcode != ATHROW) {
			onMethodFinally(opcode);
		}
	}
	
	private void onMethodFinally(int opcode) {

		push(index);
		loadLocal(localCallIndex);
		myLoadThis();

		if (opcode != ATHROW) {
			invokeStatic(PROFILER, PROFILER_ON_METHOD_EXIT);
		} else {
			invokeStatic(PROFILER, PROFILER_ON_METHOD_EXIT_EXCEPTION);
		}
		final Type retType = Type.getReturnType(super.methodDesc);
		
		push(index);
		loadLocal(localCallIndex);
		invokeStatic(PROFILER, PROFILER_ON_RETURN_VALUE);

		switch (opcode) {
		case ARETURN:
			dup();
			onTraceObject();
			dup();
			onDumpObject(TYPE_RETURN, -2);
			break;
		case LRETURN:
			dup2();
			onTracePrimitive(Type.LONG_TYPE);
			break;
		case DRETURN:
			dup2();
			onTracePrimitive(Type.DOUBLE_TYPE);
			break;
		case ATHROW:
			onTraceException();
			break;
		case RETURN:
			onTraceVoid();
			break;
		default:
			dup();
			onTracePrimitive(retType);
			break;
		}
	}
}
