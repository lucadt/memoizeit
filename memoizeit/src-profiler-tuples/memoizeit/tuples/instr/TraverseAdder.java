package memoizeit.tuples.instr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import memoizeit.asm.Debug;
import memoizeit.asm.util.AsmHelper;
import memoizeit.asm.util.Util;
import memoizeit.tuples.serialization.Methods;
import memoizeit.tuples.serialization.Types;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

public final class TraverseAdder implements Methods, Types {

	private final ClassNode cn;
	private final ClassVisitor cv;
	
	private final Type thisType;
	private final Type thisSuperType;
	private final Type serializerType;

	private GeneratorAdapter mv;
	
	public TraverseAdder(final ClassNode cn, final ClassVisitor cv) {
		this.cn = cn;
		this.cv = cv;
		this.thisType = Type.getType("L"+cn.name+";");
		this.thisSuperType = Type.getType("L"+cn.superName+";");
		if (Options.useCustomHashCode()) {
			serializerType = SERILIAZER_HASHER_HELPER;
		} else {
			serializerType = SERILIAZER_JSON_HELPER;
		}
	}
	
	public void generate() {
		mv = new GeneratorAdapter(
				Opcodes.ACC_PUBLIC + Opcodes.ACC_SYNTHETIC, 
				TRAVERSER_MY_TRAVERSE, 
				null, 
				null, 
				cv);
		onGenerateTraverseMethod();	
	}
			
	public void onPrimitive(final Type type) {
		
		switch (type.getSort()) {
		
		case Type.BOOLEAN:
			mv.invokeVirtual(serializerType, HELPER_ON_BOOLEAN);
			break;
		case Type.BYTE:
			mv.invokeVirtual(serializerType, HELPER_ON_BYTE);
			break;
		case Type.INT:
			mv.invokeVirtual(serializerType, HELPER_ON_INT);
			break;
		case Type.LONG:
			mv.invokeVirtual(serializerType, HELPER_ON_LONG);
			break;
		case Type.SHORT:
			mv.invokeVirtual(serializerType, HELPER_ON_SHORT);
			break;
		case Type.FLOAT:
			mv.invokeVirtual(serializerType, HELPER_ON_FLOAT);
			break;
		case Type.DOUBLE:
			mv.invokeVirtual(serializerType, HELPER_ON_DOUBLE);
			break;
		case Type.CHAR:
			mv.invokeVirtual(serializerType, HELPER_ON_CHAR);
			break;
		default:
			Debug.getInstance().debug("MemoizeIt.TraverseAdder", "No specific type dispatching for " + type.getClassName());
			break;
		}
		
	}
	
	private List<FieldNode> createSortedFields(final List<FieldNode> fields) {
		final List<FieldNode> sFields = new ArrayList<FieldNode>(fields);
		Collections.sort(sFields, new Comparator<FieldNode>() {
			@Override
			public int compare(final FieldNode fA, final FieldNode fB) {
				return fA.name.compareTo(fB.name);
			}
		});
		return sFields;
	}
			
	private void onGenerateTraverseMethod() {

		@SuppressWarnings("unchecked")
		final List<FieldNode> sFields = createSortedFields(cn.fields);
		final Iterator<FieldNode> it = sFields.iterator();
		
		while (it.hasNext()) {
			
			final FieldNode field = it.next();
			
			if (AsmHelper.isStatic(field.access) || 
				AsmHelper.isSynthetic(field.access) ||
				AsmHelper.isArtificial(field.name) ||
				field.name.equals("memoizeit_thr_loc_id") ||
				field.name.equals("memoizeit_thr_loc_metadata") ) {
				continue;
			}

			final Type fieldType = Type.getType(field.desc);
			final String fieldName = thisType.getClassName() + '.' + field.name;
				
			if (fieldType.getSort() == Type.OBJECT || fieldType.getSort() == Type.ARRAY) {
				mv.loadArg(0);
				mv.loadThis();
				mv.getField(thisType, field.name, fieldType);
				mv.loadArg(1);
				mv.push(fieldName);
				mv.invokeVirtual(serializerType, HELPER_ON_TRAVERSE_FIELD);
			} else {
				mv.loadArg(0);
				mv.push(fieldName);
				mv.invokeVirtual(serializerType, HELPER_ON_OBJECT_FIELD);
				mv.loadArg(0);
				mv.loadThis();
				mv.getField(thisType, field.name, fieldType);
				onPrimitive(fieldType);
			}
				
		}
		
		if (Util.isToProfile(thisSuperType.getInternalName())) {
			mv.loadThis();
			mv.loadArgs();
			mv.visitMethodInsn(Opcodes.INVOKESPECIAL, thisSuperType.getInternalName(), TRAVERSER_MY_TRAVERSE.getName(), TRAVERSER_MY_TRAVERSE.getDescriptor());
		} else {
			mv.loadArg(0);
			mv.loadThis();
			mv.loadArg(1);
			mv.push(thisSuperType.getClassName());
			mv.invokeVirtual(serializerType, HELPER_ON_SUPER_REFLECTION);
		}
		mv.returnValue();
		mv.endMethod();
	}
	
}
