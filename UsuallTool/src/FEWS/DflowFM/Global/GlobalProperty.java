package FEWS.DflowFM.Global;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GlobalProperty {

	public static void runMdu(String mduPath) throws IOException, InterruptedException {
		List<String> commandLine = new ArrayList<>();

		// start commandLine
		commandLine.add("cmd");
		commandLine.add("/c");

		// setting path
		commandLine.add(
				"path E:\\Dflow-FM\\software\\plugins\\DeltaShell.Dimr\\kernels\\x64\\dflowfm\\bin;E:\\Dflow-FM\\software\\plugins\\DeltaShell.Dimr\\kernels\\x64\\dimr\\bin;E:\\Dflow-FM\\software\\plugins\\DeltaShell.Dimr\\kernels\\x64\\share\\bin;E:\\Dflow-FM\\software\\bin\\gdal\\x64;E:\\Dflow-FM\\software\\bin\\gdal\\x64\\plugins;C:\\Program Files (x86)\\Intel\\iCLS Client\\;C:\\Program Files\\Intel\\iCLS Client\\;C:\\ProgramData\\Oracle\\Java\\javapath;C:\\Windows\\system32;C:\\Windows;C:\\Windows\\System32\\Wbem;C:\\Windows\\System32\\WindowsPowerShell\\v1.0\\;C:\\Program Files (x86)\\NVIDIA Corporation\\PhysX\\Common;C:\\Program Files (x86)\\Intel\\UCRT\\;C:\\Program Files\\Intel\\UCRT\\;C:\\Program Files (x86)\\Intel\\Intel(R) Management Engine Components\\DAL;C:\\Program Files\\Intel\\Intel(R) Management Engine Components\\DAL;C:\\Program Files (x86)\\Intel\\Intel(R) Management Engine Components\\IPT;C:\\Program Files\\Intel\\Intel(R) Management Engine Components\\IPT;C:\\Windows\\system32\\wbem;C:\\Program Files (x86)\\IVT Corporation\\BlueSoleil\\Mobile;F:\\Qgis\\3.4\\bin;F:\\python");

		// direct to MDU file
		String workDirection = new File(mduPath).getParent();
		System.out.println("workDirection : " + workDirection);
		// setting DflowFM workFile
		commandLine.add(
				"E:\\Dflow-FM\\software\\plugins\\DeltaShell.Dimr\\kernels\\x64\\BatchRunDirection\\dflowfm-cli.exe --autostartstop "
						+ mduPath);

		long startTime = System.currentTimeMillis();
		ProcessBuilder builder = new ProcessBuilder();
		builder.directory(new File(workDirection));
		builder.command(commandLine);
		Process process = builder.start();
		synchronized (process) {
			process.wait();
		}
		System.out.println("Dflow-FM ended in " + (System.currentTimeMillis() - startTime) + "milliSecond");
	}

}
