package memoizeit.analysis.version;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import memoizeit.analysis.version.Method.Tuple;
import memoizeit.asm.Debug;
import memoizeit.asm.Files;
import memoizeit.asm.profiler.text.AbstractTextFileWriter;
import memoizeit.asm.util.ids.MethodId;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public final class Output extends AbstractTextFileWriter {
		
	private final Table<String, String, List<Tuple>> table;
		
	public Output(final String path) {
		super(path, Files.TUPLES_OUTPUT);
		this.table = HashBasedTable.create();
		MethodId.getInstance().onImport(path + '/' + Files.METHODS_FILE);
	}
	
	public void print(final Map<Integer, Method> methods) {
		
		for (final Entry<Integer, Method> method : methods.entrySet()) {
			final Method myMethod = method.getValue();
			if (myMethod.getCalls().size() > 0) {
				final String mSignature = MethodId.getInstance().getSignature(method.getKey());
				final String mName = MethodId.getMethodReturn(mSignature) + ' ' + MethodId.getMethodNameWithParameters(mSignature);
				final String mClassName = MethodId.getClassName(mSignature);
				final List<Tuple> tuples = myMethod.summarize();
				if (table.contains(mClassName, mName)) {
					Debug.getInstance().debug("Version.Output", String.format("%s %s pair already existing", mClassName, mName));
				}
				table.put(mClassName, mName, tuples);										
			}
		}
						
		for (final String className : table.rowKeySet()) {
			
			final Map<String, List<Tuple>> classMethods = table.row(className);
			
			if (classMethods.size() > 0) {
				getOutput().println(className);
				for (final Entry<String, List<Tuple>> entry : classMethods.entrySet()) {
					if (entry.getValue().size() > 0) {
						getOutput().println(" " + entry.getKey());
						Collections.sort(entry.getValue(), new Comparator<Tuple>() {
							@Override
							public int compare(Tuple arg0, Tuple arg1) {
								return arg0.count - arg1.count;
							}
						});
						for (final Tuple tuple : entry.getValue()) {
							getOutput().println("  " + tuple.invoke + " " + tuple.count);
							getOutput().flush();
						}				
					}	
				}
			}
		}
	}

}
