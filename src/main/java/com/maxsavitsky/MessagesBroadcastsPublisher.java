package com.maxsavitsky;

import com.maxsavteam.ciconia.annotation.Component;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

@Component
public class MessagesBroadcastsPublisher {

	private final InetAddress inetAddress = InetAddress.getByName("255.255.255.255");
	private final DatagramSocket socket;
	private final int port;

	public MessagesBroadcastsPublisher() throws SocketException, UnknownHostException {
		port = Main.getProgramArguments().getMessagesBroadcastingPort();
		socket = new DatagramSocket();
	}

	public void sendBroadcast(String message) throws IOException {
		byte[] bytes = message.getBytes();

		DatagramPacket packet = new DatagramPacket(bytes, bytes.length, inetAddress, port);
		socket.send(packet);
	}

}
