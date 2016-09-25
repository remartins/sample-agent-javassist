package br.com.bytechameleon.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.junit.Assert;
import org.junit.Test;

import com.sun.tools.attach.VirtualMachine;

public class ByteChameleonAgentTest {

	public static void initalizeAgent() {

		String nameOfRunningVM = ManagementFactory.getRuntimeMXBean().getName();
		int p = nameOfRunningVM.indexOf('@');
		String pid = nameOfRunningVM.substring(0, p);

		String agentParam = PrintMessage.class.getName() + ":getMessage";

		File jarFile = loadAgentClass(Agent.class.getName(), agentParam, null, true, true, false);

		try {
			VirtualMachine vm = VirtualMachine.attach(pid);
			vm.loadAgent(jarFile.getPath(), PrintMessage.class.getName() + ":getMessage");
			vm.detach();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	@Test
	public void agentLoaded() {
		initalizeAgent();

		PrintMessage p = new PrintMessage();
		System.out.println(p.getMessage());
		Assert.assertTrue(p.getMessage().equals("message from agent !"));
	}




	public static File loadAgentClass(final String agentClass, final String options, final String bootClassPath,
			final boolean canRedefineClasses, final boolean canRetransformClasses,
			final boolean canSetNativeMethodPrefix) {
		final File jarFile;
		try {
			jarFile = createTemporaryAgentJar(agentClass, bootClassPath, canRedefineClasses, canRetransformClasses,
					canSetNativeMethodPrefix);
		} catch (IOException ex) {
			throw new RuntimeException("Can't write jar file for agent:" + agentClass, ex);
		}
		return jarFile;
	}


	public static File createTemporaryAgentJar(final String agentClass, final String bootClassPath,
			final boolean canRedefineClasses, final boolean canRetransformClasses,
			final boolean canSetNativeMethodPrefix) throws IOException {
		final File jarFile = File.createTempFile("javaagent." + agentClass, ".jar");
		jarFile.deleteOnExit();
		createAgentJar(new FileOutputStream(jarFile), agentClass, bootClassPath, canRedefineClasses,
				canRetransformClasses, canSetNativeMethodPrefix);
		return jarFile;
	}

	public static void createAgentJar(final OutputStream out, final String agentClass, final String bootClassPath,
			final boolean canRedefineClasses, final boolean canRetransformClasses,
			final boolean canSetNativeMethodPrefix) throws IOException {
		final Manifest man = new Manifest();
		man.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");

		man.getMainAttributes().putValue("Agent-Class", agentClass);

		if (bootClassPath != null) {
			man.getMainAttributes().putValue("Boot-Class-Path", bootClassPath);
		}
		man.getMainAttributes().putValue("Can-Redefine-Classes", Boolean.toString(canRedefineClasses));
		man.getMainAttributes().putValue("Can-Retransform-Classes", Boolean.toString(canRetransformClasses));
		man.getMainAttributes().putValue("Can-Set-Native-Method-Prefix", Boolean.toString(canSetNativeMethodPrefix));
		final JarOutputStream jarOut = new JarOutputStream(out, man);
		jarOut.flush();
		jarOut.close();
	}


}
