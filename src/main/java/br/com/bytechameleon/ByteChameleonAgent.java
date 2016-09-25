package br.com.bytechameleon;


import java.lang.instrument.Instrumentation;

public class ByteChameleonAgent {
	
	public static void premain(String agentArgs, Instrumentation inst) {
		System.out.println("ByteChameleon Agent started !");
		inst.addTransformer(new Transformer(agentArgs));
		System.out.println("ByteChameleon Agent finished !");
	}
	
	public static void agentmain(String agentArgs, Instrumentation inst) {
		premain(agentArgs, inst);
	}

}
