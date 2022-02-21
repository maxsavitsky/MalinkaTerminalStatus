package com.maxsavitsky.sections;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.terminal.Terminal;
import com.maxsavitsky.Line;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MessagesSection extends Section {

	private static final MessagesSection instance = new MessagesSection();
	private static final int COLUMNS_OFFSET = Section.WIDTH / 2 + 1;
	private static final int ROWS_OFFSET = 0;
	private static final int COLUMNS_LIMIT = Section.WIDTH - COLUMNS_OFFSET;
	private static final int ROWS_LIMIT = Section.HEIGHT;
	private final ArrayList<Message> messages = new ArrayList<>();
	private int currentLineIndex = 0;

	public static MessagesSection getInstance() {
		return instance;
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
		var messageParts = message.getMessageParts();
		int availableLines = ROWS_LIMIT - currentLineIndex + ROWS_OFFSET - 1;
		if (availableLines >= message.getLinesCount()) {
			for (int i = 0; i < messageParts.size(); i++, currentLineIndex++) {
				terminal.setCursorPosition(new TerminalPosition(COLUMNS_OFFSET, currentLineIndex));
				terminal.putString(messageParts.get(i));
			}
		} else {
			rewrite(terminal);
		}
	}

	private void rewrite(Terminal terminal) throws IOException {
		int line = ROWS_LIMIT + ROWS_OFFSET - 1;
		int messageIndex = messages.size() - 1;
		while (line >= ROWS_OFFSET) {
			Message m = messages.get(messageIndex);
			for (int i = m.getLinesCount() - 1; i >= 0 && line >= ROWS_OFFSET; i--, line--) {
				terminal.setCursorPosition(new TerminalPosition(COLUMNS_OFFSET, line));
				terminal.putString(m.getMessageParts().get(i));
				for (int j = m.getMessageParts().get(i).length(); j < COLUMNS_LIMIT; j++) {
					terminal.putCharacter(' ');
				}
			}
			if (line >= ROWS_OFFSET)
				messageIndex--;
		}
	}

	private static class Message {
		private final ArrayList<String> messageParts = new ArrayList<>();

		public Message(String s) {
			for (int i = 0; i < s.length(); i += COLUMNS_LIMIT) {
				messageParts.add(
						s.substring(i, Math.min(i + COLUMNS_LIMIT, s.length()))
				);
			}
		}

		public int getLinesCount() {
			return messageParts.size();
		}

		public List<String> getMessageParts() {
			return messageParts;
		}
	}

}
