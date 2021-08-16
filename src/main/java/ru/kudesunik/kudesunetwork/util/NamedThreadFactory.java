package ru.kudesunik.kudesunetwork.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Named thread factory for tasks and daemon executors
 * @author Kudesunik
 *
 */
public class NamedThreadFactory implements ThreadFactory {
	
	private final String threadName;
	private final boolean isNumbered;
	private final AtomicInteger threadCount;
	
	private Thread thread;
	
	public NamedThreadFactory(String threadName, boolean isNumbered) {
		this.threadName = threadName;
		this.isNumbered = isNumbered;
		this.threadCount = new AtomicInteger(1);
	}
	
	public Thread getThread() {
		return thread;
	}
	
	@Override
	public Thread newThread(Runnable runnable) {
		Thread thread = new Thread(runnable);
		if(isNumbered) {
			thread.setName(threadName + " - " + threadCount.getAndIncrement());
		} else {
			thread.setName(threadName);
		}
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.setDaemon(true);
		this.thread = thread;
		return thread;
	}
}
