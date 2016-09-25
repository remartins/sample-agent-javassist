package br.com.bytechameleon.test;

import java.lang.instrument.Instrumentation;

import br.com.bytechameleon.ByteChameleonAgent;

public class Agent {
	
	public static void agentmain(String agentArgs, Instrumentation inst) {
		ByteChameleonAgent.premain(agentArgs, inst);	
	}

}
