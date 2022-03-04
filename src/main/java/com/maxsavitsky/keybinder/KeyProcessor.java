package com.maxsavitsky.keybinder;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.util.ArrayList;

public class KeyProcessor {

	private static final KeyProcessor instance = new KeyProcessor();

	public static KeyProcessor getInstance() {
		return instance;
	}

	private final ArrayList<ShutdownHook> shutdownHooks = new ArrayList<>();

	private KeyProcessor(){}

	protected void process(KeyStroke keyStroke){
		if(isShutdownKeyStroke(keyStroke)){
			for(var s : shutdownHooks)
				s.onPreShutdown();
			System.exit(0);
		}
	}

	private boolean isShutdownKeyStroke(KeyStroke keyStroke){
		return keyStroke.getKeyType() == KeyType.Character && keyStroke.getCharacter() == 'x' && keyStroke.isCtrlDown()
				|| keyStroke.getKeyType() == KeyType.Escape;
	}

	@FunctionalInterface
	public interface ShutdownHook {
		void onPreShutdown();
	}

	public void addShutdownHook(ShutdownHook shutdownHook){
		shutdownHooks.add(shutdownHook);
	}

}
