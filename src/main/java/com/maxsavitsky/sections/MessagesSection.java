package com.maxsavitsky.sections;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.terminal.Terminal;
import com.maxsavitsky.Line;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MessagesSection extends Section {

	private static final MessagesSection instance = new MessagesSection();

	public static MessagesSection getInstance() {
		return instance;
	}

	private static final int TIME_PREFIX_LEN = 14;

	private static final int columnsOffset = Section.WIDTH / 2 + 1;
	private static final int rowsOffset = 0;

	private static final int columnsLimit = Section.WIDTH - columnsOffset;
	private static final int rowsLimit = Section.HEIGHT;

	private final ArrayList<Message> messages = new ArrayList<>();

	private int currentLineIndex = 0;

	@Override
	public String getIdentifier() {
		return "msg";
	}

	@Override
	public void write(Line line, Terminal terminal) throws IOException {
		if(line.getMessage() == null)
			return;
		String time = new SimpleDateFormat("HH:mm:ss.SSS").format(new Date(line.getTime()));
		String fullMessage = "[" + time + "] " + line.getMessage();

		Message message = new Message(fullMessage);
		messages.add(message);
		var messageParts = message.getMessageParts();
		int availableLines = rowsLimit - currentLineIndex + rowsOffset - 1;
		if(availableLines >= message.getLinesCount()){
			for(int i = 0; i < messageParts.size(); i++, currentLineIndex++){
				terminal.setCursorPosition(new TerminalPosition(columnsOffset, currentLineIndex));
				terminal.putString(messageParts.get(i));
			}
		}else{
			rewrite(terminal);
		}
	}

	private void rewrite(Terminal terminal) throws IOException {
		int line = rowsLimit + rowsOffset - 1;
		int messageIndex = messages.size() - 1;
		while(line >= rowsOffset){
			Message m = messages.get(messageIndex);
			for(int i = m.getLinesCount() - 1; i >= 0 && line >= rowsOffset; i--, line--){
				terminal.setCursorPosition(new TerminalPosition(columnsOffset, line));
				terminal.putString(m.getMessageParts().get(i));
				for(int j = m.getMessageParts().get(i).length(); j < columnsLimit; j++){
					terminal.putCharacter(' ');
				}
			}
			if(line >= rowsOffset)
				messageIndex--;
		}
	}

	public void log(String message, Terminal terminal) throws IOException {
		Line l = new Line(null, getIdentifier());
		l.setMessage(message);
		write(l, terminal);
	}

	private static class Message{
		private final ArrayList<String> messageParts = new ArrayList<>();

		public Message(String s){
			for(int i = 0; i < s.length(); i += columnsLimit){
				messageParts.add(
						s.substring(i, Math.min(i + columnsLimit, s.length()))
				);
			}
		}

		public int getLinesCount(){
			return messageParts.size();
		}

		public ArrayList<String> getMessageParts() {
			return messageParts;
		}
	}

}
