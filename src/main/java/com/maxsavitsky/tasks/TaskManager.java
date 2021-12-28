package com.maxsavitsky.tasks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TaskManager {

	private static TaskManager instance;
	// contains lists of tasks for each period to execute them all at the same time
	private final HashMap<Long, ArrayList<Task>> tasksHashMap = new HashMap<>();
	// contains timers for each period
	private final HashMap<Long, Timer> timerHashMap = new HashMap<>();

	private TaskManager() {

	}

	public static TaskManager getInstance() {
		if (instance == null)
			instance = new TaskManager();
		return instance;
	}

	public void schedule(final long period, Task task) {
		if (tasksHashMap.containsKey(period)) {
			tasksHashMap.get(period).add(task);
			return;
		}

		tasksHashMap.put(period, new ArrayList<>(List.of(task)));

		Timer timer = new Timer(true);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				ArrayList<Task> tasks = tasksHashMap.getOrDefault(period, new ArrayList<>());
				for (Task t : tasks) {
					try {
						t.execute();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}, 2000, period);
		timerHashMap.put(period, timer);
	}

}
