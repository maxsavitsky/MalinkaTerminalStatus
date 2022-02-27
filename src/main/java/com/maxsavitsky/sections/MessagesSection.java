package com.maxsavitsky.sections;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.Terminal;
import com.maxsavitsky.Line;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MessagesSection extends Section {

	private static MessagesSection instance;
	private int columnsOffset;
	private int rowsOffset;
	private int columnsLimit;
	private int rowsLimit;
	private final ArrayList<Message> messages = new ArrayList<>();
	private int currentLineIndex = 0;

	public static MessagesSection getInstance() {
		return instance;
	}

	public static MessagesSection init(TerminalScreen terminalScreen){
		instance = new MessagesSection(terminalScreen);
		return instance;
	}

	private MessagesSection(TerminalScreen terminalScreen){
		changeSizes(terminalScreen.getTerminalSize());
	}

	private void changeSizes(TerminalSize terminalSize){
		int columns = terminalSize.getColumns();
		int rows = terminalSize.getRows();
		columnsOffset = columns / 2;
		rowsOffset = 0;
		columnsLimit = columns - columnsOffset;
		rowsLimit = rows;
	}

	@Override
	public void onTerminalSizeChange(Terminal terminal, TerminalSize newTerminalSize) throws IOException {
		changeSizes(newTerminalSize);
		rewrite(terminal);
	}

	@Override
	public String getIdentifier() {
		return "msg";
	}

	@Override
	public void write(Line line, Terminal terminal) throws IOException {
		if (line.getMessage() == null)
			return;
		String time = new SimpleDateFormat("HH:mm:ss.SSS dd.MM").format(new Date(line.getTime()));
		String fullMessage = "[" + time + "] " + line.getMessage();

		Message message = new Message(fullMessage);
		messages.add(message);
		var messageParts = message.getMessageParts(columnsLimit);
		int availableLines = rowsLimit - currentLineIndex + rowsOffset - 1;
		if (availableLines >= messageParts.size()) {
			for (int i = 0; i < messageParts.size(); i++, currentLineIndex++) {
				terminal.setCursorPosition(new TerminalPosition(columnsOffset, currentLineIndex));
				terminal.putString(messageParts.get(i));
			}
		} else {
			rewriteFromEnd(terminal);
		}
	}

	private void rewrite(Terminal terminal) throws IOException {
		currentLineIndex = rowsOffset;
		ArrayList<String> lines = new ArrayList<>();
		for(Message m : messages){
			List<String> messageParts = m.getMessageParts(columnsLimit);
			lines.addAll(messageParts);
		}
		int offset = 0;
		if(lines.size() > rowsLimit)
			offset = lines.size() - rowsLimit;
		currentLineIndex = 0;
		for(int i = offset; i < lines.size(); i++, currentLineIndex++){
			terminal.setCursorPosition(new TerminalPosition(columnsOffset, currentLineIndex));
			terminal.putString(lines.get(i));
			clearLine(terminal, columnsLimit - lines.get(i).length());
		}
		for(int j = currentLineIndex; j < rowsLimit; j++){
			terminal.setCursorPosition(new TerminalPosition(columnsOffset, currentLineIndex));
			clearLine(terminal, columnsLimit);
		}
	}

	private void rewriteFromEnd(Terminal terminal) throws IOException {
		int line = rowsLimit + rowsOffset - 1;
		int messageIndex = messages.size() - 1;
		while (line >= rowsOffset) {
			Message m = messages.get(messageIndex);
			List<String> parts = m.getMessageParts(columnsLimit);
			for (int i = parts.size() - 1; i >= 0 && line >= rowsOffset; i--, line--) {
				terminal.setCursorPosition(new TerminalPosition(columnsOffset, line));
				terminal.putString(parts.get(i));
				clearLine(terminal, columnsLimit - parts.get(i).length());
			}
			if (line >= rowsOffset)
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
