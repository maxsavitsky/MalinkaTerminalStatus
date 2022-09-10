package com.maxsavitsky;

import com.maxsavteam.ciconia.annotation.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

@Component
public class MessagesListener {

	private static final int BUFFER_MAX_SIZE = 100;
	private final ArrayList<String> buffer = new ArrayList<>();
	private final ArrayList<ListenerCallback> listenerCallbacks = new ArrayList<>();
	private ServerSocket serverSocket;
	private final static Object BUFFER_LOCK = new Object();

	public MessagesListener() {
		int port = Main.getProgramArguments().getPort();
		new Thread(() -> {
			try {
				serverSocket = new ServerSocket(port);
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}

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

	public void addListener(ListenerCallback callback) {
		listenerCallbacks.add(callback);
	}

	private void addToBuffer(String s) {
		synchronized(BUFFER_LOCK) {
			buffer.add(s);
			while (buffer.size() > BUFFER_MAX_SIZE)
				buffer.remove(0);
		}
	}

	public void fetchBuffer(ListenerCallback callback) {
		synchronized (BUFFER_LOCK) {
			for (String s : buffer)
				callback.onMessage(s);
		}
	}

	public void handle(String s) {
		for (var callback : listenerCallbacks)
			callback.onMessage(s);
	}

	public interface ListenerCallback {
		void onMessage(String s);
	}

}
