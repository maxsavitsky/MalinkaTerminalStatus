package com.maxsavitsky;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MessagesListener {

	private static final int BUFFER_MAX_SIZE = 100;

	public interface ListenerCallback {
		void onMessage(String s);
	}

	private static final MessagesListener instance = new MessagesListener();

	public static MessagesListener getInstance() {
		return instance;
	}

	private ServerSocket serverSocket;

	private final ArrayList<String> buffer = new ArrayList<>();

	private final ArrayList<ListenerCallback> listenerCallbacks = new ArrayList<>();

	public void addListener(ListenerCallback callback) {
		listenerCallbacks.add(callback);
	}

	private MessagesListener() {
		new Thread(() -> {
			try {
				serverSocket = new ServerSocket(8000);
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			System.out.println("Socket started after " + (System.currentTimeMillis() - Main.startTime)/1000.0 + " seconds");
			while (!Thread.currentThread().isInterrupted()) {
				try {

					try (Socket socket = serverSocket.accept();
					     BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
						String s;
						while ((s = reader.readLine()) != null) {
							addToBuffer(s);
							for(var l : listenerCallbacks)
								l.onMessage(s);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}).start();
	}

	private void addToBuffer(String s) {
		buffer.add(s);
		while (buffer.size() > BUFFER_MAX_SIZE)
			buffer.remove(0);
	}

	public ArrayList<String> getBuffer() {
		return new ArrayList<>(buffer);
	}

	public void fetchBuffer(ListenerCallback callback){
		for(String s : buffer)
			callback.onMessage(s);
	}

}
