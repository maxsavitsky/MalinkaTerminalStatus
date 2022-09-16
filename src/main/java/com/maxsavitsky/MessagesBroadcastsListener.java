package com.maxsavitsky;

import com.maxsavitsky.manager.MessagesDispatcher;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MessagesBroadcastsListener {

	public MessagesBroadcastsListener(MessagesDispatcher dispatcher, int port){
		new Thread(()->{
			try (DatagramSocket socket = new DatagramSocket(port, InetAddress.getByName("0.0.0.0"))) {
				byte[] buffer = new byte[1024];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				while (!Thread.currentThread().isInterrupted()) {
					socket.receive(packet);
					String s = new String(packet.getData(), 0, packet.getLength());
					dispatcher.printMessage(s);
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}).start();
	}

}
