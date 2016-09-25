package br.com.bytechameleon;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

public class Transformer implements ClassFileTransformer {

	private String className;
	private String methodName;

	public Transformer(String agentArgs) {
		String[] args = agentArgs.split(":");
		this.className = args[0].replace('.', '/');
		this.methodName = args[1];
	}

	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

		byte[] byteCode = classfileBuffer;

		try {
			ClassPool classPool = ClassPool.getDefault();
			CtClass ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));

			if (className.equals(this.className)) {

				CtMethod method = ctClass.getDeclaredMethod(methodName);

				method.setBody("return \"message from agent !\";");

				method.insertBefore("System.out.println(\"Saiu antes\");");
				method.insertAfter("System.out.println(\"Saiu depois\");");

				byteCode = ctClass.toBytecode();
				ctClass.detach();

				System.out.println("Instrumentation complete.");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return byteCode;
	}

}
