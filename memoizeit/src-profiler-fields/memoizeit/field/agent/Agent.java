package memoizeit.field.agent;

import java.lang.instrument.Instrumentation;

import memoizeit.field.instr.Instrument;

public class Agent {	
	public static void premain(String agentArgs, Instrumentation inst) {
		inst.addTransformer(new Instrument());			
	}
}
