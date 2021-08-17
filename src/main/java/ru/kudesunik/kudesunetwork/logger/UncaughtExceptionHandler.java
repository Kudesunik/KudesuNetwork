package ru.kudesunik.kudesunetwork.logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.MarkerManager;

public class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
	
	@Override
	public void uncaughtException(Thread thread, Throwable throwable) {
		LogManager.getRootLogger().fatal(MarkerManager.getMarker("ExceptionMarker"), "Uncaught exception in thread " + thread.getName(), throwable);
	}
}
