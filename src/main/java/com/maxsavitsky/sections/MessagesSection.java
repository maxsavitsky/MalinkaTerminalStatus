package com.maxsavitsky.sections;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.terminal.Terminal;
import com.maxsavitsky.Content;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MessagesSection extends Section {
	private final ArrayList<Message> messages = new ArrayList<>();
	private int currentLineIndex = 0;

	@Override
	public SectionSize declareSize(TerminalSize terminalSize) {
		int columns = terminalSize.getColumns();
		int rows = terminalSize.getRows();
		int offsetX = columns / 2;
		int offsetY = 0;
		int width = columns - offsetX;
		return new SectionSize(
				width,
				rows,
				offsetX,
				offsetY
		);
	}

	@Override
	public void onTerminalSizeChange(Terminal terminal, TerminalSize newTerminalSize) throws IOException {
		rewrite(terminal);
	}

	@Override
	public String getIdentifier() {
		return "msg";
	}

	@Override
	public void write(Content content, Terminal terminal) throws IOException {
		if (content.getMessage() == null)
			return;
		String time = new SimpleDateFormat("HH:mm:ss.SSS dd.MM").format(new Date(content.getTime()));
		String fullMessage = "[" + time + "] " + content.getMessage();

		Message message = new Message(fullMessage);
		messages.add(message);
		List<String> messageParts = message.getMessageParts(getLastDeclaredSize().getWidth());
		int availableLines = getLastDeclaredSize().getHeight() - currentLineIndex;
		if (availableLines >= messageParts.size()) {
			for (int i = 0; i < messageParts.size(); i++, currentLineIndex++) {
				terminal.setCursorPosition(new TerminalPosition(0, currentLineIndex));
				terminal.putString(messageParts.get(i));
			}
		} else {
			rewriteFromEnd(terminal);
		}
	}

	private void rewrite(Terminal terminal) throws IOException {
		SectionSize size = getLastDeclaredSize();
		ArrayList<String> lines = new ArrayList<>();
		for(Message m : messages){
			List<String> messageParts = m.getMessageParts(size.getHeight());
			lines.addAll(messageParts);
		}
		int offset = 0;
		if(lines.size() > size.getHeight()) // if we have more lines than we can display we draw from the end
			offset = lines.size() - size.getHeight();
		currentLineIndex = 0;
		for(int i = offset; i < lines.size(); i++, currentLineIndex++){
			terminal.setCursorPosition(new TerminalPosition(0, currentLineIndex));
			terminal.putString(lines.get(i));
			clearLine(terminal, size.getWidth() - lines.get(i).length());
		}
		for(int j = currentLineIndex; j < size.getHeight(); j++){
			terminal.setCursorPosition(new TerminalPosition(0, currentLineIndex));
			clearLine(terminal, size.getWidth());
		}
	}

	private void rewriteFromEnd(Terminal terminal) throws IOException {
		SectionSize size = getLastDeclaredSize();
		int line = size.getHeight() - 1;
		int messageIndex = messages.size() - 1;
		while (line >= 0) {
			Message m = messages.get(messageIndex);
			List<String> parts = m.getMessageParts(size.getWidth());
			for (int i = parts.size() - 1; i >= 0 && line >= 0; i--, line--) {
				terminal.setCursorPosition(new TerminalPosition(0, line));
				terminal.putString(parts.get(i));
				clearLine(terminal, size.getWidth() - parts.get(i).length());
			}
			messageIndex--;
		}
	}

	private static class Message {

		private final String msg;
		private int lastColumnsLimit = -1;
		private ArrayList<String> lastParts;

		public Message(String s) {
			this.msg = s;
		}

		public List<String> getMessageParts(int columnsLimit) {
			if(columnsLimit == lastColumnsLimit)
				return lastParts;
			lastColumnsLimit = columnsLimit;
			lastParts = new ArrayList<>();
			for (int i = 0; i < msg.length(); i += columnsLimit) {
				lastParts.add(
						msg.substring(i, Math.min(i + columnsLimit, msg.length()))
				);
			}
			return lastParts;
		}
	}

}
