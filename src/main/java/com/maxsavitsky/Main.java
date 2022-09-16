package com.maxsavitsky;

import com.maxsavitsky.tasks.ServicesStatsTask;
import com.maxsavitsky.tasks.provider.service.DefaultServicesInfoProvider;
import com.maxsavitsky.tasks.provider.service.LinuxServicesInfoProvider;
import com.maxsavitsky.tasks.provider.service.ServicesInfoProvider;
import com.maxsavitsky.tasks.provider.system.DefaultSystemInfoProvider;
import com.maxsavitsky.tasks.provider.system.LinuxSystemInfoProvider;
import com.maxsavitsky.tasks.provider.system.SystemInfoProvider;
import com.maxsavteam.ciconia.annotation.Configuration;
import com.maxsavteam.ciconia.sparkjava.CiconiaSparkApplication;
import com.maxsavteam.ciconia.sparkjava.CiconiaSparkConfiguration;
import org.apache.commons.lang3.SystemUtils;
import oshi.SystemInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configuration
public class Main {

	private static final SystemInfo SYSTEM_INFO = new SystemInfo();

	public static SystemInfo getSystemInfo() {
		return SYSTEM_INFO;
	}

	private static ProgramArguments programArguments;

	public static ProgramArguments getProgramArguments() {
		return programArguments;
	}

	public static void main(String[] args) throws IOException {
		if (args.length > 0 && args[0].equals("help")) {
			System.out.println(Utils.getHelpString());
			return;
		}

		programArguments = ProgramArgumentsParser.parse(args);

		if (programArguments.getMailPropertiesFile() != null)
			MailSender.init(programArguments.getMailPropertiesFile());

		CiconiaSparkConfiguration configuration = new CiconiaSparkConfiguration.Builder()
				.setPort(programArguments.getApiPort())
				.setExceptionHandler((exception, request, response) -> exception.printStackTrace())
				.build();

		CiconiaSparkApplication.run(Main.class, configuration);

		String afterStartupCommand = programArguments.getAfterStartupCommand();
		if (afterStartupCommand != null && !afterStartupCommand.isEmpty()) {
			Utils.exec(afterStartupCommand);
		}
	}

	public static SystemInfoProvider getLocalSystemInfoProvider() {
		if (SystemUtils.IS_OS_LINUX)
			return new LinuxSystemInfoProvider();
		return new DefaultSystemInfoProvider();
	}

	public static ServicesInfoProvider getLocalServicesInfoProvider() {
		if (SystemUtils.IS_OS_LINUX)
			return new LinuxServicesInfoProvider();
		return new DefaultServicesInfoProvider();
	}

	public static List<ServicesStatsTask.Service> getServicesFromFile(String path) {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(path))))) {
			List<ServicesStatsTask.Service> services = new ArrayList<>();
			String content = reader.readLine();
			int lineIndex = 1;
			while (content != null) {
				if (content.isEmpty() || content.startsWith("#"))
					continue;
				int p = content.indexOf(':');
				if (p == -1) {
					throw new IllegalArgumentException("Illegal format (should be `id:name`) at content " + lineIndex);
				}
				String serviceId = content.substring(0, p);
				String serviceName = content.substring(p + 1);
				services.add(new ServicesStatsTask.Service(serviceId, serviceName));

				content = reader.readLine();
				lineIndex++;
			}
			return services;
		} catch (IOException e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

}
