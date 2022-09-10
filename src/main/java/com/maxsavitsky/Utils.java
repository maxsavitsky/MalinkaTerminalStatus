package com.maxsavitsky;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Utils {

	private static final Runtime runtime = Runtime.getRuntime();

	public static String exec(String cmd) throws IOException {
		Process process = runtime.exec(cmd);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		process.getInputStream().transferTo(bos);
		return bos.toString();
	}

	public static String getHelpString(){
		return
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
					""";
	}

}
