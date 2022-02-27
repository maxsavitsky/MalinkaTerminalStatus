package com.maxsavitsky;

import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.maxsavitsky.sections.MessagesSection;
import com.maxsavitsky.sections.SystemStatusSection;
import com.maxsavitsky.tasks.SystemStatTask;
import com.maxsavitsky.tasks.TaskManager;
import com.maxsavitsky.tasks.TempControlTask;
import org.apache.commons.lang3.SystemUtils;
import oshi.SystemInfo;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {

	private static final SystemInfo SYSTEM_INFO = new SystemInfo();

	public static SystemInfo getSystemInfo() {
		return SYSTEM_INFO;
	}

	private static long startTime;

	public static long getStartTime() {
		return startTime;
	}

	public static void main(String[] args) throws IOException {
		if(args.length > 0 && args[0].equals("help")){
			System.out.println(
					"""
					Arguments:
					\t--tty=PATH_TO_TTY_DEV_BLOCK
					\t\tSpecifies where terminal should be displayed.
					\t\tInput and output streams will be redirected.
					\t\tFor example, --tty=dev/tty4
					
					\t--disable-temp-control
					\t\tDisables temperature control (notifications, emergency shutdown).
					
					\t--execute-after-startup=COMMAND
					\t\tThis command will be executed after terminal will be ready to display data.
					
					\t--port=PORT
					\t\tSpecifies port which socket will listen.
					
					\t--services-list=path_to_file
					\t\tThis file describes the services whose status should be displayed.
					\t\tEach service is described by a separate line in the format 'id:name`
					\t\tid should be identifier of service in systemctl
					\t\tNote: Works only on linux
					"""
			);
			return;
		}
		startTime = System.currentTimeMillis();

		MailSender.getInstance();

		boolean enableTempControl = true;
		boolean enableServicesStats = true;
		String afterStartupCommand = null;
		int port = 8000;
		String pathToServicesList = null;

		InputStream is = System.in;
		OutputStream os = System.out;
		for(String arg : args){
			if(arg.startsWith("--tty=")){
				String tty = arg.substring(6);
				os = new FileOutputStream(tty);
				is = new FileInputStream(tty);
				System.out.println("tty set to " + tty);
			}else if(arg.equals("--disable-temp-control")){
				enableTempControl = false;
				System.out.println("WARNING! Temperature control disabled");
			}else if(arg.startsWith("--execute-after-startup=")) {
				afterStartupCommand = arg.substring("--execute-after-startup=".length());
			}else if(arg.startsWith("--port=")) {
				port = Integer.parseInt(arg.substring("--port=".length()));
			} else if(arg.startsWith("--services-list=")){
				pathToServicesList = arg.substring("--services-list=".length());
			} else{
				throw new IllegalArgumentException("Unknown argument '" + arg + "'");
			}
		}

		MessagesListener.init(port);

		TerminalScreen terminalScreen = new DefaultTerminalFactory(os, is, StandardCharsets.UTF_8)
				.createScreen();
		terminalScreen.startScreen();
		terminalScreen.doResizeIfNecessary();
		Terminal terminal = terminalScreen.getTerminal();
		terminal.setCursorVisible(false);
		MessagesController.addSection(new SystemStatusSection(terminalScreen));
		MessagesController.addSection(MessagesSection.init(terminalScreen));
		MessagesController.start(terminal);

		terminal.addResizeListener(MessagesController::onTerminalSizeChange);

		KeyHandler.start(terminal);

		String msg = "Messages controller started after " + (System.currentTimeMillis() - startTime) / 1000.0 + " seconds";
		MessagesSection.getInstance().write(
				new Line("t", "msg", msg),
				terminal
		);

		MessagesSection.getInstance().write(
				new Line("t", "msg", "Started as " + Utils.exec("whoami")),
				terminal
		);

		List<SystemStatTask.Service> services;
		if(pathToServicesList == null)
			services = Collections.emptyList();
		else
			services = getServicesFromFile(pathToServicesList);
		TaskManager.getInstance().schedule(SystemStatTask.TIMER_PERIOD, new SystemStatTask(enableServicesStats, services));

		if (SystemUtils.IS_OS_LINUX && enableTempControl) {
			TaskManager.getInstance().schedule(TempControlTask.TIMER_PERIOD, new TempControlTask());
		}

		if(afterStartupCommand != null && !afterStartupCommand.isEmpty()){
			Utils.exec(afterStartupCommand);
		}
	}

	private static List<SystemStatTask.Service> getServicesFromFile(String path){
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)))){
			ArrayList<SystemStatTask.Service> services = new ArrayList<>();
			String line = reader.readLine();
			int lineIndex = 1;
			while(line != null){
				if(line.isEmpty() || line.startsWith("#"))
					continue;
				int p = line.indexOf(':');
				if(p == -1){
					throw new IllegalArgumentException("Illegal format (should be `id:name`) at line " + lineIndex);
				}
				String serviceId = line.substring(0, p);
				String serviceName = line.substring(p + 1);
				services.add(new SystemStatTask.Service(serviceId, serviceName));

				line = reader.readLine();
				lineIndex++;
			}
			return services;
		}catch (IOException e){
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

}
