package memoizeit.field.profiler;

import memoizeit.asm.Manager;
import memoizeit.asm.util.DumpHelper;
import memoizeit.asm.util.ids.FieldId;
import memoizeit.asm.util.ids.MethodId;
import memoizeit.field.instr.Options;

public final class MyManager extends Manager {
	
	private static final MyManager INSTANCE = new MyManager();
	
	public static synchronized MyManager getInstance() {
        return INSTANCE;
    }
		
	@Override
	protected void onPostProcessThreads() {
		DumpHelper.writeStringToFile(Options.getMethodsFile(), MethodId.getInstance().onExport());
		DumpHelper.writeStringToFile(Options.getFieldsFile(), FieldId.getInstance().onExport());
	}

}
