package ru.kudesunik.kudesunetwork.logger;

import java.net.URI;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.FilterComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.LayoutComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.RootLoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

public class LoggerConfigurationFactory extends ConfigurationFactory {
	
	private final boolean useConsoleLogging;
	private final boolean useFileLogging;
	private final Level level;
	
	public LoggerConfigurationFactory(boolean useConsoleLogging, boolean useFileLogging, Level level) {
		this.useConsoleLogging = useConsoleLogging;
		this.useFileLogging = useFileLogging;
		this.level = level;
	}
	
	@Override
	public Configuration getConfiguration(LoggerContext loggerContext, ConfigurationSource source) {
		return getConfiguration(loggerContext, source.toString(), null);
	}
	
	@Override
	public Configuration getConfiguration(LoggerContext loggerContext, String name, URI configLocation) {
		return createConfiguration(name, newConfigurationBuilder());
	}
	
	private Configuration createConfiguration(String name, ConfigurationBuilder<BuiltConfiguration> builder) {
		
		//Initial section
		
		builder.setConfigurationName(name);
		builder.setStatusLevel(Level.ERROR);
		
		//Layout section
		
		LayoutComponentBuilder standardLayoutBuilder = builder.newLayout("PatternLayout");
		standardLayoutBuilder.addAttribute("pattern", "%d{dd-MM-yy hh:mm:ss (SSS)} [%t] %-5level: %msg%n%throwable");
		
		LayoutComponentBuilder crashFileLayoutBuilder = builder.newLayout("PatternLayout");
		crashFileLayoutBuilder.addAttribute("alwaysWriteExceptions", false);
		
		//Console system out logging
		
		AppenderComponentBuilder consoleSystemOutAppenderBuilder = builder.newAppender("SystemOut", "CONSOLE");
		consoleSystemOutAppenderBuilder.addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT);
		
		FilterComponentBuilder systemOutFilterComponentBuilder = builder.newFilter("ThresholdFilter", Filter.Result.DENY, Filter.Result.ACCEPT);
		systemOutFilterComponentBuilder.addAttribute("level", Level.WARN);
		
		consoleSystemOutAppenderBuilder.add(standardLayoutBuilder);
		consoleSystemOutAppenderBuilder.add(systemOutFilterComponentBuilder);
		builder.add(consoleSystemOutAppenderBuilder);
		
		//Console system err logging
		
		AppenderComponentBuilder consoleSystemErrAppenderBuilder = builder.newAppender("SystemError", "CONSOLE");
		consoleSystemErrAppenderBuilder.addAttribute("target", ConsoleAppender.Target.SYSTEM_ERR);
		
		FilterComponentBuilder systemErrFilterComponentBuilder = builder.newFilter("ThresholdFilter", Filter.Result.ACCEPT, Filter.Result.DENY);
		systemErrFilterComponentBuilder.addAttribute("level", Level.WARN);
		
		consoleSystemErrAppenderBuilder.add(standardLayoutBuilder);
		consoleSystemErrAppenderBuilder.add(systemErrFilterComponentBuilder);
		builder.add(consoleSystemErrAppenderBuilder);
		
		//Default file logging
		
		ComponentBuilder<?> triggeringPolicyDefault = builder.newComponent("Policies");
		triggeringPolicyDefault.addComponent(builder.newComponent("OnStartupTriggeringPolicy").addAttribute("minSize", "0"));
		
		ComponentBuilder<?> rolloverStrategy = builder.newComponent("DefaultRolloverStrategy");
		rolloverStrategy.addAttribute("fileIndex", "nomax");
		
		ComponentBuilder<?> rolloverStrategyDelete = builder.newComponent("Delete").addAttribute("basePath", "log/archive").addAttribute("maxDepth", "1");
		ComponentBuilder<?> rolloverStrategyDeleteIfLastModified = builder.newComponent("IfLastModified").addAttribute("age", "10d");
		
		rolloverStrategyDelete.addComponent(rolloverStrategyDeleteIfLastModified);
		rolloverStrategy.addComponent(rolloverStrategyDelete);
		
		AppenderComponentBuilder fileAppenderBuilder = builder.newAppender("RollingFileDefault", "RollingFile");
		fileAppenderBuilder.addAttribute("fileName", "log/launch.log");
		fileAppenderBuilder.addAttribute("filePattern", "log/archive/launch-%d{yy-MM-dd}-%i.log.gz");
		fileAppenderBuilder.add(standardLayoutBuilder);
		fileAppenderBuilder.addComponent(triggeringPolicyDefault);
		fileAppenderBuilder.addComponent(rolloverStrategy);
		
		if(useFileLogging) {
			builder.add(fileAppenderBuilder);
		}
		
		//Uncaught exception file logging
		
		ComponentBuilder<?> triggeringPolicyException = builder.newComponent("Policies");
		triggeringPolicyException.addComponent(builder.newComponent("OnStartupTriggeringPolicy").addAttribute("minSize", "0"));
		
		ComponentBuilder<?> exceptionRolloverStrategy = builder.newComponent("DefaultRolloverStrategy");
		exceptionRolloverStrategy.addAttribute("fileIndex", "nomax");
		
		ComponentBuilder<?> exceptionRolloverStrategyDelete = builder.newComponent("Delete").addAttribute("basePath", "log/crash").addAttribute("maxDepth", "1");
		ComponentBuilder<?> exceptionRolloverStrategyDeleteIfAccumulatedFileCount = builder.newComponent("IfAccumulatedFileCount").addAttribute("exceeds", "10");
		
		exceptionRolloverStrategyDelete.addComponent(exceptionRolloverStrategyDeleteIfAccumulatedFileCount);
		exceptionRolloverStrategy.addComponent(exceptionRolloverStrategyDelete);
		
		FilterComponentBuilder exceptionFilterComponentBuilder = builder.newFilter("MarkerFilter", Filter.Result.ACCEPT, Filter.Result.DENY);
		exceptionFilterComponentBuilder.addAttribute("marker", "ExceptionMarker");
		
		AppenderComponentBuilder exceptionFileAppenderBuilder = builder.newAppender("RollingFileException", "RollingFile");
		exceptionFileAppenderBuilder.addAttribute("fileName", "log/crash.log");
		exceptionFileAppenderBuilder.addAttribute("filePattern", "log/crash/crash-%d{yyyy-MM-dd-HH-mm-ss}.log.gz");
		exceptionFileAppenderBuilder.add(crashFileLayoutBuilder);
		exceptionFileAppenderBuilder.add(exceptionFilterComponentBuilder);
		exceptionFileAppenderBuilder.addComponent(triggeringPolicyException);
		
		exceptionFileAppenderBuilder.addComponent(exceptionRolloverStrategy);
		
		if(useFileLogging) {
			builder.add(exceptionFileAppenderBuilder);
		}
		
		//Final section
		
		RootLoggerComponentBuilder rootLoggerComponentBuilder = builder.newAsyncRootLogger(level);
		
		if(useConsoleLogging) {
			rootLoggerComponentBuilder.add(builder.newAppenderRef("SystemOut"));
			rootLoggerComponentBuilder.add(builder.newAppenderRef("SystemError"));
		}
		if(useFileLogging) {
			rootLoggerComponentBuilder.add(builder.newAppenderRef("RollingFileDefault"));
			rootLoggerComponentBuilder.add(builder.newAppenderRef("RollingFileException"));
		}
		
		builder.add(rootLoggerComponentBuilder);
		
		return builder.build();
	}
	
	@Override
	protected String[] getSupportedTypes() {
		return new String[]{"*"};
	}
}
