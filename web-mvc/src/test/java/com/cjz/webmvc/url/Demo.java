package com.cjz.webmvc.url;

import org.apache.logging.log4j.*;
import org.junit.jupiter.api.Test;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-10-10 14:07
 */
class Demo {
	Logger log = LogManager.getLogger(Demo.class);

	@Test
	void t1() {
		log.info("calc:  1 + 1 = {}", () -> 1 + 1);
		log.printf(Level.OFF, "calc:  1 + 1 = {}", 2);
		log.printf(Level.DEBUG, "calc:  1 + 1 = {}", 2);
		log.printf(Level.INFO, "calc:  1 + 1 = {}", 2);
		log.printf(Level.WARN, "calc:  1 + 1 = {}", 2);
		log.printf(Level.ERROR, "calc:  1 + 1 = {}", 2);
	}

	@Test
	void t2() {
		Marker SQL_MARKER = MarkerManager.getMarker("SQL");
		Marker UPDATE_MARKER = MarkerManager.getMarker("SQL_UPDATE").setParents(SQL_MARKER);
		Marker QUERY_MARKER = MarkerManager.getMarker("SQL_QUERY").setParents(SQL_MARKER);
		log.info(UPDATE_MARKER, "SQL");
		log.info(UPDATE_MARKER, "SQL_UPDATE");
		log.info(QUERY_MARKER, "SQL_QUERY");

	}
}
