package memoizeit.tuples.instr.adapter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import memoizeit.asm.BaseOptions;
import memoizeit.asm.util.AsmHelper;
import memoizeit.asm.util.ids.MethodId;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;

public final class MethodFilterAdapter extends MethodVisitor {
	
	public static final Set<String> readWhiteList(final String fileName) {
		try {
			final BufferedReader reader = new BufferedReader(new FileReader(fileName));
			final Scanner scanner = new Scanner(reader);
			final Set<String> methods = new HashSet<String>();
			while(scanner.hasNextLine()) {
				final String line = scanner.nextLine();
				if (line.length() > 0) {
					methods.add(line);
				}
			}
			scanner.close();
			return methods;
		} catch (final FileNotFoundException ex) {
			ex.printStackTrace();
		}
		return new HashSet<String>();
	}

	private static final Set<String> WHITE_LIST = readWhiteList(BaseOptions.getWhiteListFile());
	
	private final MethodVisitor next;
	private final String owner;
	
	public MethodFilterAdapter(int access, String owner, String name, String desc, String signature, String[] exceptions, MethodVisitor mv) {
		super(Opcodes.ASM4, new MethodNode(access, name, desc, signature, exceptions));
		this.next = mv;
		this.owner = owner;
	}
		
	@Override 
	public void visitEnd() {
		
		final MethodNode mn = (MethodNode) mv;   
		final String method = MethodId.getSignatureNoReturn(this.owner, mn.name, mn.desc, ':');

	    if (WHITE_LIST.contains(method)) {
	    	if (AsmHelper.isConstructur(mn.name) ||
	    		AsmHelper.isStaticConstructur(mn.name) ||
	    		Type.getReturnType(mn.desc).getSort() == Type.VOID) {
	    		mn.accept(this.next);	    	
	    	} else {
	    		mn.accept(new ParametersDumpAdapter(this.next, mn.access, this.owner, mn.name, mn.desc));
	    	}
	    } else {
    		mn.accept(this.next);
	    }
	    
	}

}
