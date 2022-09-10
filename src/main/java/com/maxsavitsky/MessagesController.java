package com.maxsavitsky;

import com.maxsavitsky.manager.ContentDispatcher;
import com.maxsavteam.ciconia.annotation.Component;

import java.io.IOException;

@Component
public class MessagesController {

	private final ContentDispatcher contentDispatcher;

	public MessagesController(ContentDispatcher contentDispatcher, MessagesListener messagesListener) {
		this.contentDispatcher = contentDispatcher;
		MessagesListener.ListenerCallback listenerCallback = this::handleMessage;
		messagesListener.fetchBuffer(listenerCallback);
		messagesListener.addListener(listenerCallback);
	}

	private void handleMessage(String message) {
		String tag = null;
		String msg = null;
		String label = null;
		String secId = null;

		System.out.println("Received message " + message);

		String[] ss = message.split("~#");
		for (String s : ss) {
			int p = s.indexOf('=');
			if (p != -1) {
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
		Content content = new Content(tag, secId, msg, label);

		try {
			contentDispatcher.dispatch(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
