package memoizeit.tuples.agent;

import java.lang.instrument.Instrumentation;

import memoizeit.tuples.instr.Instrument;

public class Agent {
	public static void premain(String agentArgs, Instrumentation inst) {
		inst.addTransformer(new Instrument());			
	}
}
