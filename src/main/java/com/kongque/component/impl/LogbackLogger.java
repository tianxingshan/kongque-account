package com.kongque.component.impl;

import org.apache.commons.lang3.StringUtils;
import org.objectweb.util.monolog.wrapper.p6spy.P6SpyLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kongque.util.SysUtil;
import com.p6spy.engine.logging.Category;

public class LogbackLogger extends P6SpyLogger {

	private static final Logger logger = LoggerFactory.getLogger("p6spyLogger");

	private static final Logger logger2 = LoggerFactory.getLogger("console");
	private static final Logger logger3 = LoggerFactory.getLogger(LogbackLogger.class);

	@Override
	public void logSQL(int connectionId, String s, long l, Category category, String s1, String sql) {

			if (logger.isInfoEnabled() && StringUtils.isNotBlank(sql)) {
				logger2.info(sql);
				//前端sql打印
				if(SysUtil.getRequest() != null && ("sql").equals(SysUtil.getRequest().getHeader("sql")))
					logger3.info(sql);
				if (sql.startsWith("insert") || sql.startsWith("update") || sql.startsWith("delete"))
					logger.info(sql);
			}
	}

}