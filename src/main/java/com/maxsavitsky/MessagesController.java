package com.maxsavitsky;

import com.googlecode.lanterna.terminal.Terminal;
import com.maxsavitsky.sections.MessagesSection;
import com.maxsavitsky.sections.Section;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Scanner;

public class MessagesController {

	private static final ArrayList<Section> sections = new ArrayList<>();

	private static Terminal terminal;

	public static void addSection(Section section){
		sections.add(section);
	}

	public static void handle(Line l) throws IOException {
		for(Section s : sections){
			if(s.getIdentifier().equals(l.getSectionId())){
				s.write(l, terminal);
				terminal.flush();
			}
		}
	}

	public static void start(final Terminal terminal){
		MessagesController.terminal = terminal;
		MessagesListener.ListenerCallback callback = MessagesController::handleMessage;
		MessagesListener.getInstance().fetchBuffer(callback);
		MessagesListener.getInstance().addListener(callback);
	}

	private static void handleMessage(String message){
		String tag = null;
		String msg = null;
		String label = null;
		String secId = null;

		try {
			MessagesSection.getInstance().write(new Line(null, "Received message " + message), terminal);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Received message " + message);

		String[] ss = message.split("~#");
		for(String s : ss){
			int p = s.indexOf('=');
			if(p != -1){
				String id = s.substring(0, p);
				String content = s.substring(p + 1);
				switch (id) {
					case "tag" -> tag = content;
					case "lbl" -> label = content;
					case "msg" -> msg = content;
					case "sec" -> secId = content;
				}
			}
		}
		Line l = new Line(tag, secId, msg, label);

		try {
			handle(l);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
