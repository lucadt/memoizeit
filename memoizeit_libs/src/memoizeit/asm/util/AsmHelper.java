package memoizeit.asm.util;

import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LookupSwitchInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MultiANewArrayInsnNode;
import org.objectweb.asm.tree.TableSwitchInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.util.Printer;

public class AsmHelper {
	
	private static char PACKAGE_SEPARATOR = '/';
	private static String ACCESS_PRIVATE = "private";
	private static String ACCESS_PROTECTED = "protected";
	private static String ACCESS_PUBLIC = "public";
	private static String ACCESS_DEFAULT = "default";
	
	public static String getPackageName(final String className) {
		final int lastSeparatorIndex = className.lastIndexOf(PACKAGE_SEPARATOR);
		return className.substring(0, lastSeparatorIndex);
	}
	
	public static String getClassNameOnly(final String className) {
		final int lastSeparatorIndex = className.lastIndexOf(PACKAGE_SEPARATOR);
		return className.substring(lastSeparatorIndex);
	}
	
	public static String accessToString(int modifiers) {
		
		if (isPrivate(modifiers)) {
			return ACCESS_PRIVATE;
		} else if (isProtected(modifiers)) {
			return ACCESS_PROTECTED;
		} else if (isPublic(modifiers)) {
			return ACCESS_PUBLIC;
		} else {
			return ACCESS_DEFAULT;
		}
		
	}
	
	public static int getMethodArguments(final String desc) {
		return (Type.getArgumentsAndReturnSizes(desc) >> 2) - 1;
	}
	
	public static boolean isFlaggedAs(int flag, int modifiers) {
		return (modifiers & flag) != 0;
	}
	
	public static boolean isFinal(int modifiers) {
		return isFlaggedAs(modifiers, Opcodes.ACC_FINAL);
	}
	
	public static boolean isPublic(int modifiers) {
		return isFlaggedAs(modifiers, Opcodes.ACC_PUBLIC);
	}
	
	public static boolean isProtected(int modifiers) {
		return isFlaggedAs(modifiers, Opcodes.ACC_PROTECTED);
	}
	
	public static boolean isPrivate(int modifiers) {
		return isFlaggedAs(modifiers, Opcodes.ACC_PRIVATE);
	}
	
	public static boolean isInterface(int modifiers) {
		return isFlaggedAs(modifiers, Opcodes.ACC_INTERFACE);
	}
	
	public static boolean isAbstract(int modifiers) {
		return isFlaggedAs(modifiers, Opcodes.ACC_ABSTRACT);
	}
	
	public static boolean isStatic(int modifiers) {
		return isFlaggedAs(modifiers, Opcodes.ACC_STATIC);
	}
	
	public static boolean isSynchronized(int modifiers) {
		return isFlaggedAs(modifiers, Opcodes.ACC_SYNCHRONIZED);
	}
	
	public static boolean isVolatile(int modifiers) {
		return isFlaggedAs(modifiers, Opcodes.ACC_VOLATILE);
	}
	
	public static boolean isSynthetic(int modifiers) {
		return isFlaggedAs(modifiers, Opcodes.ACC_SYNTHETIC);
	}
	
	public static boolean isTransient(int modifiers) {
		return isFlaggedAs(modifiers, Opcodes.ACC_TRANSIENT);
	}
	
	public static boolean isNative(int modifiers) {
		return isFlaggedAs(modifiers, Opcodes.ACC_NATIVE);
	}
	
	public static boolean isConstructur(final String name) {
		return "<init>".equals(name);
	}
	
	public static boolean isStaticConstructur(final String name) {
		return "<clinit>".equals(name);
	}
	
	public static boolean isEnclosingClassField(final String name) {
		return name.startsWith("this$");
	}
	
	public static boolean isArtificial(final String name) {
		return name.contains("$");
	}
		
	public static boolean isMethodExitInstrunction(int opcode) {
		return (opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN) || opcode == Opcodes.ATHROW;
	}
	
	public static boolean isReturnInstrunction(int opcode) {
		return opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN;
	}
	
	public static boolean isThrowInstrunction(int opcode) {
		return opcode == Opcodes.ATHROW;
	}
	
	public static boolean isVirtualInstruction(int opcode) {
		return opcode == -1;
	}
	
	// Taken directly from DiSL
	public static boolean isPEInstruction(int opcode) {

		switch (opcode) {

		// NullPointerException, ArrayIndexOutOfBoundsException
		case Opcodes.BALOAD:
		case Opcodes.DALOAD:
		case Opcodes.FALOAD:
		case Opcodes.IALOAD:
		case Opcodes.LALOAD:
		case Opcodes.BASTORE:
		case Opcodes.CASTORE:
		case Opcodes.DASTORE:
		case Opcodes.FASTORE:
		case Opcodes.IASTORE:
		case Opcodes.LASTORE:
		case Opcodes.AALOAD:
		case Opcodes.CALOAD:
		case Opcodes.SALOAD:
		case Opcodes.SASTORE:
			// NullPointerException, ArrayIndexOutOfBoundsException,
			// ArrayStoreException
		case Opcodes.AASTORE:
			// NullPointerException
		case Opcodes.ARRAYLENGTH:
		case Opcodes.ATHROW:
		case Opcodes.GETFIELD:
		case Opcodes.PUTFIELD:
			// NullPointerException, StackOverflowError
		case Opcodes.INVOKEINTERFACE:
		case Opcodes.INVOKESPECIAL:
		case Opcodes.INVOKEVIRTUAL:
			// StackOverflowError
		case Opcodes.INVOKESTATIC:
			// NegativeArraySizeException
		case Opcodes.ANEWARRAY:
			// NegativeArraySizeException, OutOfMemoryError
		case Opcodes.NEWARRAY:
		case Opcodes.MULTIANEWARRAY:
			// OutOfMemoryError, InstantiationError
		case Opcodes.NEW:
			// OutOfMemoryError
		case Opcodes.LDC:
			// ClassCastException
		case Opcodes.CHECKCAST:
			// ArithmeticException
		case Opcodes.IDIV:
		case Opcodes.IREM:
		case Opcodes.LDIV:
		case Opcodes.LREM:
			// New instruction in JDK7
		case Opcodes.INVOKEDYNAMIC:
			return true;
		default:
			return false;
		}
		
	}
	
	public static String disassembleInstruction(final AbstractInsnNode instruction, final InsnList instructions) {

		final int opcode = instruction.getOpcode();
		
		final String mnemonic = opcode == -1? "" : Printer.OPCODES[instruction.getOpcode()];
		
		String instructionRep = mnemonic + " ";
		
		switch (instruction.getType()) {
		
		case AbstractInsnNode.LABEL: 
			// pseudo-instruction (branch or exception target)
			return instructionRep + "// label";
		case AbstractInsnNode.FRAME:
			// pseudo-instruction (stack frame map)
			return instructionRep + "// stack frame map";
		case AbstractInsnNode.LINE:
			// pseudo-instruction (line number information)
			return instructionRep + "// line number information";
		case AbstractInsnNode.INSN:
			// Opcodes: NOP, ACONST_NULL, ICONST_M1, ICONST_0, ICONST_1, ICONST_2,
		    // ICONST_3, ICONST_4, ICONST_5, LCONST_0, LCONST_1, FCONST_0,
			// FCONST_1, FCONST_2, DCONST_0, DCONST_1, IALOAD, LALOAD, FALOAD,
			// DALOAD, AALOAD, BALOAD, CALOAD, SALOAD, IASTORE, LASTORE, FASTORE,
			// DASTORE, AASTORE, BASTORE, CASTORE, SASTORE, POP, POP2, DUP,
			// DUP_X1, DUP_X2, DUP2, DUP2_X1, DUP2_X2, SWAP, IADD, LADD, FADD,
			// DADD, ISUB, LSUB, FSUB, DSUB, IMUL, LMUL, FMUL, DMUL, IDIV, LDIV,
			// FDIV, DDIV, IREM, LREM, FREM, DREM, INEG, LNEG, FNEG, DNEG, ISHL,
			// LSHL, ISHR, LSHR, IUSHR, LUSHR, IAND, LAND, IOR, LOR, IXOR, LXOR,
			// I2L, I2F, I2D, L2I, L2F, L2D, F2I, F2L, F2D, D2I, D2L, D2F, I2B,
			// I2C, I2S, LCMP, FCMPL, FCMPG, DCMPL, DCMPG, IRETURN, LRETURN,
			// FRETURN, DRETURN, ARETURN, RETURN, ARRAYLENGTH, ATHROW,
			// MONITORENTER, or MONITOREXIT.
			// zero operands, nothing to print
			return instructionRep;
		case AbstractInsnNode.INT_INSN:
			// Opcodes: NEWARRAY, BIPUSH, SIPUSH.
			if (instruction.getOpcode()==Opcodes.NEWARRAY) {
				// NEWARRAY
				return instructionRep + Printer.TYPES[((IntInsnNode)instruction).operand];
			} else {
				// BIPUSH or SIPUSH
				return instructionRep + ((IntInsnNode)instruction).operand;
			}
		case AbstractInsnNode.JUMP_INSN:
			// Opcodes: IFEQ, IFNE, IFLT, IFGE, IFGT, IFLE, IF_ICMPEQ,
		    // IF_ICMPNE, IF_ICMPLT, IF_ICMPGE, IF_ICMPGT, IF_ICMPLE, IF_ACMPEQ,
		    // IF_ACMPNE, GOTO, JSR, IFNULL or IFNONNULL.
		{
			final LabelNode targetInstruction = ((JumpInsnNode)instruction).label;
			final int targetId = instructions.indexOf(targetInstruction);
			return instructionRep + targetId;
		}
		case AbstractInsnNode.LDC_INSN:
			// Opcodes: LDC.
			return instructionRep + ((LdcInsnNode)instruction).cst;
		case AbstractInsnNode.IINC_INSN:
			// Opcodes: IINC.
			return instructionRep + (((IincInsnNode)instruction).var) + " " + (((IincInsnNode)instruction).incr);
		case AbstractInsnNode.TYPE_INSN:
			// Opcodes: NEW, ANEWARRAY, CHECKCAST or INSTANCEOF.
			return instructionRep + ((TypeInsnNode)instruction).desc;
		case AbstractInsnNode.VAR_INSN:
			// Opcodes: ILOAD, LLOAD, FLOAD, DLOAD, ALOAD, ISTORE,
		    // LSTORE, FSTORE, DSTORE, ASTORE or RET.
			return instructionRep + ((VarInsnNode)instruction).var;
		case AbstractInsnNode.FIELD_INSN:
			// Opcodes: GETSTATIC, PUTSTATIC, GETFIELD or PUTFIELD.
			return instructionRep + ((FieldInsnNode)instruction).owner + "." + ((FieldInsnNode)instruction).name + " " + ((FieldInsnNode)instruction).desc;
		case AbstractInsnNode.METHOD_INSN:
			// Opcodes: INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC,
		    // INVOKEINTERFACE or INVOKEDYNAMIC.
			return instructionRep + ((MethodInsnNode)instruction).owner + "." + ((MethodInsnNode)instruction).name + " " + ((MethodInsnNode)instruction).desc;
		case AbstractInsnNode.MULTIANEWARRAY_INSN:
			// Opcodes: MULTIANEWARRAY.
			return instructionRep + ((MultiANewArrayInsnNode)instruction).desc + " " + ((MultiANewArrayInsnNode)instruction).dims;
		case AbstractInsnNode.LOOKUPSWITCH_INSN:
			// Opcodes: LOOKUPSWITCH.
		{
			@SuppressWarnings("rawtypes")
			final List keys = ((LookupSwitchInsnNode)instruction).keys;
			@SuppressWarnings("rawtypes")
			final List labels = ((LookupSwitchInsnNode)instruction).labels;
			for (int t = 0; t < keys.size(); t++) {
				final int key = (Integer) keys.get(t);
				final LabelNode targetInstruction = (LabelNode) labels.get(t);
				final int targetId = instructions.indexOf(targetInstruction);
				instructionRep += key +": " + targetId +", ";
			}
			final LabelNode defaultTargetInstruction = ((LookupSwitchInsnNode)instruction).dflt;
			final int defaultTargetId = instructions.indexOf(defaultTargetInstruction);
			return instructionRep + "default: " + defaultTargetId;
		}
		case AbstractInsnNode.TABLESWITCH_INSN:
			// Opcodes: TABLESWITCH.
		{
			final int minKey = ((TableSwitchInsnNode)instruction).min;
			@SuppressWarnings("rawtypes")
			final List labels = ((TableSwitchInsnNode)instruction).labels;
			for (int t = 0; t < labels.size(); t++) {
				final int key = minKey+t;
				final LabelNode targetInstruction = (LabelNode)labels.get(t);
				final int targetId = instructions.indexOf(targetInstruction);
				instructionRep += key +": " + targetId +", ";
			}
			final LabelNode defaultTargetInstruction = ((TableSwitchInsnNode)instruction).dflt;
			final int defaultTargetId = instructions.indexOf(defaultTargetInstruction);
			return instructionRep + "default: " + defaultTargetId;
		}
		}
		
		return null;
	
	}

}
