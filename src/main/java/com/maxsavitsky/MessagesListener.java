package com.maxsavitsky;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MessagesListener {

	private static final int BUFFER_MAX_SIZE = 100;
	private static MessagesListener instance;
	private final ArrayList<String> buffer = new ArrayList<>();
	private final ArrayList<ListenerCallback> listenerCallbacks = new ArrayList<>();
	private ServerSocket serverSocket;

	public static void init(int port){
		instance = new MessagesListener(port);
	}

	private MessagesListener(int port) {
		new Thread(() -> {
			try {
				serverSocket = new ServerSocket(port);
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			String msg = "Socket started after " + (System.currentTimeMillis() - Main.getStartTime()) / 1000.0 + " seconds";
			addToBuffer("tag=t~#sec=msg~#msg=" + msg);
			while (!Thread.currentThread().isInterrupted()) {
				try {

					try (Socket socket = serverSocket.accept();
					     BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
						String s;
						while ((s = reader.readLine()) != null) {
							addToBuffer(s);
							handle(s);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}).start();
	}

	public static MessagesListener getInstance() {
		return instance;
	}

	public void addListener(ListenerCallback callback) {
		listenerCallbacks.add(callback);
	}

	private void addToBuffer(String s) {
		buffer.add(s);
		while (buffer.size() > BUFFER_MAX_SIZE)
			buffer.remove(0);
	}

	public void fetchBuffer(ListenerCallback callback) {
		for (String s : buffer)
			callback.onMessage(s);
	}

	public void handle(String s) {
		for (var l : listenerCallbacks)
			l.onMessage(s);
	}

	public interface ListenerCallback {
		void onMessage(String s);
	}

}
