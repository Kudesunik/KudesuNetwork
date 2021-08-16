package ru.kudesunik.kudesunetwork.util.task;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import org.apache.logging.log4j.Level;

import ru.kudesunik.kudesunetwork.KudesuNetwork;
import ru.kudesunik.kudesunetwork.util.NamedThreadFactory;

public class TaskManager {
	
	private static TaskManager taskManager;
	
	private static TaskManager get() {
		TaskManager instance = taskManager;
		if(instance == null) {
			synchronized(TaskManager.class) {
				instance = taskManager;
				if(instance == null) {
					taskManager = new TaskManager();
				}
			}
		}
		return taskManager;
	}
	
	private final ThreadFactory threadFactory;
	private final ScheduledExecutorService executor;
	
	private TaskManager() {
		this.threadFactory = new NamedThreadFactory("Task manager thread", true);
		this.executor = Executors.newScheduledThreadPool(1, threadFactory);
	}
	
	public static ScheduledFuture<?> execute(TaskManagerTask task) {
		ScheduledExecutorService executor = TaskManager.get().executor;
		ScheduledFuture<?> result = executor.scheduleAtFixedRate(() -> update(task), task.getInitialDelay(), task.getUpdateTime(), TimeUnit.MILLISECONDS);
		task.setResult(result);
		return result;
	}
	
	public static ScheduledFuture<?> executeOnce(Runnable runnable, int lifeTime) {
		TaskManagerTask task = new TaskManagerTask(runnable);
		task.setStopCondition(t -> (t.getCurrentExecutions() >= 1) || ((System.currentTimeMillis() + lifeTime) < t.getTime()));
		return execute(task);
	}
	
	public static ScheduledFuture<?> executeOnce(Runnable runnable, String name, int lifeTime) {
		TaskManagerTask task = new TaskManagerTask(name, runnable);
		task.setStopCondition(t -> (t.getCurrentExecutions() >= 1) || ((System.currentTimeMillis() + lifeTime) < t.getTime()));
		return execute(task);
	}
	
	public static ScheduledFuture<?> checkAndExecuteOnce(Runnable runnable, Predicate<TaskManagerTask> updateCondition, int lifeTime) {
		TaskManagerTask task = new TaskManagerTask(runnable);
		task.setUpdateCondition(updateCondition);
		final long checkTime = System.currentTimeMillis() + lifeTime;
		task.setStopCondition(t -> (t.getCurrentExecutions() >= 1) || (checkTime < t.getTime()));
		return execute(task);
	}
	
	private static void update(TaskManagerTask task) {
		task.updateTime();
		if(task.getUpdateCondition().test(task)) {
			try {
				task.call();
			} catch(Exception ex) {
				KudesuNetwork.log(Level.ERROR, "[TaskManager] '" + task.getName() + "' task execution failed!");
				ex.printStackTrace();
				task.getResult().cancel(true);
			}
			task.updateCurrentExecutions();
		}
		if(task.getStopCondition().test(task)) {
			task.getResult().cancel(false);
		}
	}
	
	public static void shutdown() {
		TaskManager.get().executor.shutdown();
	}
}
