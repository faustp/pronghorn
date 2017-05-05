package com.pccw.bpss.qa.automation.utility;


import org.apache.log4j.Logger;

/**
 * Created by FaustineP on 06/03/2017.
 */
public class Log {

    private static Logger logger = Logger.getLogger(Log.class.getName());

    public static void info(String message) {
        logger.info(message);
    }

    public static void warn(String message) {
        logger.warn(message);
    }

    public static void error(String message) {
        logger.error(message);
    }

    public static void debug(String message) {
        logger.debug(message);
    }

    public static void fatal(String message) {
        logger.fatal(message);
    }

    public static void tracr(String message) {
        logger.trace(message);
    }

}
