package ru.kudesunik.kudesunetwork.util.task;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Predicate;

public final class TaskManagerTask implements Callable<Void> {
	
	private final String name;
	private final Runnable task;
	
	private ScheduledFuture<?> result;
	
	private long initialDelay;
	private long updateTime;
	
	private long time;
	
	private int currentExecutions;
	
	private Predicate<TaskManagerTask> updateCondition;
	private Predicate<TaskManagerTask> stopCondition;
	
	public TaskManagerTask(String name, Runnable task) {
		this.name = name;
		this.task = task;
		this.initialDelay = 0L;
		this.updateTime = 100L;
		this.time = 0;
		this.currentExecutions = 0;
		this.updateCondition = (TaskManagerTask t) -> true;
		this.stopCondition = (TaskManagerTask t) -> false;
	}
	
	public TaskManagerTask(Runnable task) {
		this("Unnamed", task);
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public Void call() throws Exception {
		task.run();
		return null;
	}
	
	public synchronized void setResult(ScheduledFuture<?> result) {
		this.result = result;
	}
	
	public synchronized ScheduledFuture<?> getResult() {
		return result;
	}
	
	public synchronized long getInitialDelay() {
		return initialDelay;
	}
	
	public synchronized TaskManagerTask setInitialDelay(long initialDelay) {
		this.initialDelay = initialDelay;
		return this;
	}
	
	public synchronized long getUpdateTime() {
		return updateTime;
	}
	
	public synchronized TaskManagerTask setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
		return this;
	}
	
	public synchronized long getTime() {
		return time;
	}
	
	public synchronized void updateTime() {
		this.time = System.currentTimeMillis();
	}
	
	public synchronized void updateCurrentExecutions() {
		currentExecutions++;
	}
	
	public synchronized int getCurrentExecutions() {
		return currentExecutions;
	}
	
	public synchronized Predicate<TaskManagerTask> getUpdateCondition() {
		return updateCondition;
	}
	
	public synchronized TaskManagerTask setUpdateCondition(Predicate<TaskManagerTask> updateCondition) {
		this.updateCondition = updateCondition;
		return this;
	}
	
	public synchronized Predicate<TaskManagerTask> getStopCondition() {
		return stopCondition;
	}
	
	public synchronized TaskManagerTask setStopCondition(Predicate<TaskManagerTask> stopCondition) {
		this.stopCondition = stopCondition;
		return this;
	}
}