package memoizeit.analysis.fields;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import memoizeit.analysis.AbstractMain;
import memoizeit.analysis.fields.ICallback;
import memoizeit.analysis.fields.Trace;
import memoizeit.asm.BaseOptions;
import memoizeit.asm.Debug;
import memoizeit.asm.Files;
import memoizeit.asm.TargetAccesses;
import memoizeit.asm.TargetAccesses.Accesses;
import memoizeit.asm.profiler.text.AbstractTextFileWriter;
import memoizeit.asm.util.DumpHelper;
import memoizeit.asm.util.ids.FieldId;
import memoizeit.asm.util.ids.MethodId;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

public final class Main extends AbstractMain implements ICallback {

	@Override
	public void onSetup(final String path) {
		FieldId.getInstance().onImport(path + '/' + Files.FIELDS_FILE);
		MethodId.getInstance().onImport(path + '/' + Files.METHODS_FILE);
	}

	@Override
	public void onShutdown(final String path) {
		final TargetAccesses accesses = new TargetAccesses();
		final Output stats = new Output(path, Files.STATS_FILE);		
		stats.onStart();
		for (final Cell<Integer, String, Set<Integer>> cell : profiles.cellSet()) {
		
			final int mIdentifier = cell.getRowKey();
			final String mTargetType = cell.getColumnKey();
			final Set<Integer> mTargetFields = cell.getValue();
			
			final String mName = MethodId.getInstance().getSignature(mIdentifier);
				
			final Accesses target = accesses.getOrCreate(mIdentifier, mTargetType);
			
			final List<String> sortedAccessedFields = new ArrayList<String>();
			for (int field : mTargetFields) {
				final String fSignature = FieldId.getInstance().getSignature(field);
				final String fName = FieldId.getFieldName(fSignature);
				final String fClassName = FieldId.getFieldClass(fSignature);
				target.add(fClassName, fName);
				sortedAccessedFields.add(fClassName + ':' + fName);
			}			
			Collections.sort(sortedAccessedFields, new Comparator<String>() {
				@Override
				public int compare(final String arg0, String arg1) {
					return arg0.compareToIgnoreCase(arg1);
				}				
			});
			
			int sReads = 0;
			if (staticReads.containsKey(mIdentifier)) {
				sReads = staticReads.get(mIdentifier).size();
			}
			
			int sWrites = 0;
			if (staticWrites.containsKey(mIdentifier)) {
				sWrites = staticWrites.get(mIdentifier).size();
			}
									
			stats.write(String.format("%s:%s (%d,%d)", mTargetType, mName, sReads, sWrites));
			for (final String field : sortedAccessedFields) {
				stats.write(' ' + field);
			}
			
		}
		stats.onShutdown();
		DumpHelper.writeObjectToFile(path + '/' + Files.FIELDS_OUTPUT, accesses);		
	}
	
	@Override
	public void onDirectory(final String path, final String directory) {
		new Trace(directory, Files.FIELDS_TRACE, this).read();
		onPrintStatistics(path);
		onReset();
	}

	private void onPrintStatistics(final String path) {
		Debug.getInstance().debug("Main.Field", path);
		Debug.getInstance().debug("Main.Field", Integer.toString(stack.size()));
	}
	
	private void onReset() {
		stack.clear();
	}
	
	public static void main(String[] args) {
		new Main().onMain(BaseOptions.getFieldsDirectory());
	}
	
	private static final class Output extends AbstractTextFileWriter {
		public Output(final String filePath, final String fileName) {
			super(filePath, fileName);
		}
		
		public void write(final String output) {
			getOutput().println(output);
			getOutput().flush();
		}
	}

	private static final class Frame {
		
		private final int index;
		private final int target;
		private final Set<Integer> writes;
		
		public Frame(int index, int target) {
			this.index = index;
			this.target = target;
			this.writes = new HashSet<Integer>();
		}
			
		public static final Frame create(int index, int target) {
			return new Frame(index, target);
		}
		
		public void onWrite(int field) {
			writes.add(field);
		}
		
		public boolean onRead(int field) {
			return writes.contains(field);
		}
		
	}
		
	private final Stack<Frame> stack = new Stack<Frame>();
	private final Table<Integer, String, Set<Integer>> profiles = HashBasedTable.create();
	private final Map<Integer, Set<Integer>> staticReads = new HashMap<Integer, Set<Integer>>();
	private final Map<Integer, Set<Integer>> staticWrites = new HashMap<Integer, Set<Integer>>();
	private final Map<Integer, String> types = new HashMap<Integer, String>();
	
	private void onCheckTOS(int index, int target) {
		assert !stack.isEmpty();
		final Frame top = stack.peek();
		assert top.target == target && top.index == index;
		if (top.target != target && top.index != index) {
			Debug.getInstance().debug("Main.Field", "Expecting " + index + " found " + top.index);
		}
	}
	
	private void onCheckType(int target, final String targetType) {
		if (target == 0) { return; }
		if (types.containsKey(target)) {
			final String currentType = types.get(target);
			if (!currentType.equals(targetType)) {
				Debug.getInstance().debug("Main.Field", "Expecting " + currentType + " found " + targetType);							
			}
		} else {
			types.put(target, targetType);
		}
	}
	
	@Override
	public void onEntry(int index, int target, final String targetType) {
		onCheckType(target, targetType);
		stack.push(Frame.create(index, target));
	}

	@Override
	public void onExit(int index, int target) {
		onCheckTOS(index, target);
		Frame frame = null;
		while (!stack.isEmpty()) {
			frame = stack.pop();
			if (frame.index == index && frame.target == target)
				break;
		}
		assert frame.index == index && frame.target == target;
	}

	@Override
	public void onExitException(int index, int target) {
		Debug.getInstance().debug("Main.Field", "onException: " + index + " " + target + " " + stack.size());
	}

	@Override
	public void onFieldRead(int index, int field, int target) {
		onCheckTOS(index, target);
		for (final Frame frame : stack) {
			if (frame.target == target) {
				if (!frame.onRead(field)) {
					final String type = types.get(target);
					if (!profiles.contains(frame.index, type)) {
						profiles.put(frame.index, type, new HashSet<Integer>());
					}
					profiles.get(frame.index, type).add(field);
				}
			}
		}
	}

	@Override
	public void onFieldWrite(int index, int field, int target) {
		onCheckTOS(index, target);
		for (final Frame frame : stack) {
			if (frame.target == target) {
				frame.onWrite(field);
			}
		}
	}

	@Override
	public void onFieldStaticRead(int index, int field) { 
		if (!staticReads.containsKey(index)) {
			staticReads.put(index, new HashSet<Integer>());			
		}
		staticReads.get(index).add(field);
	}

	@Override
	public void onFieldStaticWrite(int index, int field) { 
		if (!staticWrites.containsKey(index)) {
			staticWrites.put(index, new HashSet<Integer>());			
		}
		staticWrites.get(index).add(field);
	}

}
