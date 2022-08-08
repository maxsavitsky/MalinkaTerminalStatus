package com.maxsavitsky;

import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.maxsavitsky.keybinder.KeyHandler;
import com.maxsavitsky.keybinder.KeyProcessor;
import com.maxsavitsky.sections.MessagesSection;
import com.maxsavitsky.sections.SystemStatusSection;
import com.maxsavitsky.tasks.SystemStatTask;
import com.maxsavitsky.tasks.TaskManager;
import com.maxsavitsky.tasks.TempControlTask;
import com.maxsavitsky.tasks.provider.DefaultSystemInfoProvider;
import com.maxsavitsky.tasks.provider.LinuxSystemInfoProvider;
import com.maxsavitsky.tasks.provider.SystemInfoProvider;
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
					ESC or Ctrl-X to exit (sometimes Enter should be pressed)

					Arguments:
					\t--tty=<path to console dev block>
					\t\tSpecifies where terminal should be displayed.
					\t\tInput and output streams will be redirected.
					\t\tFor example, --tty=/dev/tty4
					\t\tDefault is current terminal
					
					\t--temp-control-enabled=<true/false>
					\t\tEnables or disables temperature control (notifications, emergency shutdown).
					\t\tDefault is true
					
					\t--temp-control-period=<period in seconds>
					\t\tSpecifies how often temperature checks will take place
					\t\tDefault is 15
					
					\t--system-status-update-period=<period in seconds>
					\t\tSpecifies how often system information will be updated
					\t\tDefault is 15
					
					\t--execute-after-startup=<command>
					\t\tThis command will be executed after terminal will be ready to display data.
					
					\t--execute-before-shutdown=<command>
					\t\tThis command will be executed before terminal will be closed.
					
					\t--port=<port>
					\t\tSpecifies port which socket will listen.
					\t\tDefault is 8000
					
					\t--services-list=<path>
					\t\tThis file describes the services whose status should be displayed.
					\t\tEach service is described by a separate content in the format id:name
					\t\tid should be identifier of service in systemctl
					\t\tNote: Works only on linux
					
					\t--mail-properties=<path>
					\t\tFile describes mail configuration in key=value format.
					\t\tIf not specified, notifications will not be sent
					\t\tIt should contain:
					\t\t- mail - describes mail address on whose behalf the message will be sent
					\t\t- pass - password
					\t\t- recipients-list - comma-separated list of recipients
					\t\t- smtp-host - smtp host which will be used for mail sending (e.g. smtp.gmail.com)
					"""
			);
			return;
		}
		startTime = System.currentTimeMillis();

		boolean enableTempControl = true;
		boolean enableServicesStats = true;
		String afterStartupCommand = null;
		String beforeShutdownCommand = null;
		int port = 8000;
		String pathToServicesList = null;
		String mailPropertiesFile = null;

		long tempControlPeriod = 15;
		long sysStatPeriod = 1;

		InputStream is = System.in;
		OutputStream os = System.out;
		for(String arg : args){
			if(arg.startsWith("--tty=")){
				String tty = arg.substring("--tty=".length());
				os = new FileOutputStream(tty);
				is = new FileInputStream(tty);
			}else if(arg.startsWith("--temp-control-enabled=")){
				enableTempControl = Boolean.parseBoolean(arg.substring("--temp-control-enabled=".length()));
			}else if(arg.startsWith("--execute-after-startup=")) {
				afterStartupCommand = arg.substring("--execute-after-startup=".length());
			}else if(arg.startsWith("--execute-before-shutdown=")) {
				beforeShutdownCommand = arg.substring("--execute-before-shutdown=".length());
			}else if(arg.startsWith("--port=")) {
				port = Integer.parseInt(arg.substring("--port=".length()));
			} else if(arg.startsWith("--services-list=")){
				pathToServicesList = arg.substring("--services-list=".length());
			} else if(arg.startsWith("--mail-properties=")) {
				mailPropertiesFile = arg.substring("--mail-properties=".length());
			}else if(arg.startsWith("--temp-control-period=")){
				tempControlPeriod = Long.parseLong(arg.substring("--temp-control-period=".length()));
			}else if(arg.startsWith("--system-status-update-period=")) {
				sysStatPeriod = Long.parseLong(arg.substring("--system-status-update-period=".length()));
			} else{
				throw new IllegalArgumentException("Unknown argument '" + arg + "'");
			}
		}

		MessagesListener.init(port);

		if(mailPropertiesFile != null)
			MailSender.init(mailPropertiesFile);

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

		String finalBeforeShutdownCommand = beforeShutdownCommand;
		KeyProcessor.getInstance().addShutdownHook(()->{
			try {
				if(finalBeforeShutdownCommand != null && !finalBeforeShutdownCommand.isEmpty())
					Utils.exec(finalBeforeShutdownCommand);
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				terminalScreen.stopScreen();
				terminal.clearScreen();
				terminal.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		String msg = "Messages controller started after " + (System.currentTimeMillis() - startTime) / 1000.0 + " seconds";
		MessagesSection.getInstance().write(
				new Content("t", "msg", msg),
				terminal
		);

		MessagesSection.getInstance().write(
				new Content("t", "msg", "Started as " + Utils.exec("whoami")),
				terminal
		);

		List<SystemStatTask.Service> services;
		if(pathToServicesList == null)
			services = Collections.emptyList();
		else
			services = getServicesFromFile(pathToServicesList);
		TaskManager.getInstance().schedule(sysStatPeriod * 1000, new SystemStatTask(enableServicesStats, services, getSystemInfoProvider()));

		if (enableTempControl) {
			TaskManager.getInstance().schedule(tempControlPeriod * 1000, new TempControlTask());
		}

		if(afterStartupCommand != null && !afterStartupCommand.isEmpty()){
			Utils.exec(afterStartupCommand);
		}
	}

	public static SystemInfoProvider getSystemInfoProvider(){
		if(SystemUtils.IS_OS_LINUX)
			return new LinuxSystemInfoProvider();
		return new DefaultSystemInfoProvider();
	}

	private static List<SystemStatTask.Service> getServicesFromFile(String path){
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)))){
			ArrayList<SystemStatTask.Service> services = new ArrayList<>();
			String content = reader.readLine();
			int lineIndex = 1;
			while(content != null){
				if(content.isEmpty() || content.startsWith("#"))
					continue;
				int p = content.indexOf(':');
				if(p == -1){
					throw new IllegalArgumentException("Illegal format (should be `id:name`) at content " + lineIndex);
				}
				String serviceId = content.substring(0, p);
				String serviceName = content.substring(p + 1);
				services.add(new SystemStatTask.Service(serviceId, serviceName));

				content = reader.readLine();
				lineIndex++;
			}
			return services;
		}catch (IOException e){
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

}
