package memoizeit.tuples.profiler;

import memoizeit.asm.BaseOptions;
import memoizeit.asm.Manager;
import memoizeit.asm.util.DumpHelper;
import memoizeit.asm.util.ids.MethodId;
import memoizeit.tuples.instr.Options;
import memoizeit.tuples.serialization.AbstractSerializerHelper;

public final class MyManager extends Manager {
	
	private static final MyManager INSTANCE = new MyManager();
	
	public static synchronized MyManager getInstance() {
        return INSTANCE;
    }
		
	@Override
	protected void onPostProcessThreads() {
		if (Options.useMaxDepth() && AbstractSerializerHelper.isMaximumDepthReached()) {
			DumpHelper.writeStringToFile(BaseOptions.getMaximumDepthFile(), "");			
		}
		DumpHelper.writeStringToFile(Options.getMethodsFile(), MethodId.getInstance().onExport());
	}

}
