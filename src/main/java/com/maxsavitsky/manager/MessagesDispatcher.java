package com.maxsavitsky.manager;

import com.maxsavitsky.Content;
import com.maxsavitsky.MessagesBroadcastsPublisher;
import com.maxsavteam.ciconia.annotation.Component;

import java.io.IOException;

@Component
public class MessagesDispatcher {

	private final ContentDispatcher contentDispatcher;
	private final MessagesBroadcastsPublisher messagesBroadcastsPublisher;

	public MessagesDispatcher(ContentDispatcher contentDispatcher, MessagesBroadcastsPublisher messagesBroadcastsPublisher) {
		this.contentDispatcher = contentDispatcher;
		this.messagesBroadcastsPublisher = messagesBroadcastsPublisher;
	}

	public void handleAndPrintMessage(String message) {
		printMessage(message);

		try {
			messagesBroadcastsPublisher.sendBroadcast(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void printMessage(String message){
		try {
			contentDispatcher.dispatch(new Content(
					null,
					"msg",
					message
			));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
