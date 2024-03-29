package com.maxsavitsky.tasks;

import com.maxsavteam.ciconia.annotation.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

@Component
public class TaskManager {

	// contains lists of tasks for each period to execute them all at the same time
	private final HashMap<Long, ArrayList<Task>> tasksHashMap = new HashMap<>();
	// contains timers for each period
	private final HashMap<Long, Timer> timerHashMap = new HashMap<>();

	public void schedule(final long period, Task task) {
		if (tasksHashMap.containsKey(period)) {
			tasksHashMap.get(period).add(task);
			return;
		}

		tasksHashMap.put(period, new ArrayList<>(Collections.singletonList(task)));

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
		}, 100, period);
		timerHashMap.put(period, timer);
	}

}
