package com.yq.test;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;

public class Log4jTest {
    public static void main(String argv[]) {
        MessageDigestPasswordEncoder passwordEncoder = new MessageDigestPasswordEncoder("MD5");
        // Create a logger by the name of class Log4jTest.
        Logger logger = Logger.getLogger(Log4jTest.class);

        // Use the default configuration.
//        BasicConfigurator.configure();

        // Set the logger level to Level.INFO
//        logger.setLevel(Level.INFO);

        // This request will be disabled since Level.DEBUG < Level.INFO.
        String a = passwordEncoder.encodePassword("Qazxcv@123", "");
        logger.debug("This is debug." + a);

        // These requests will be enabled.
        logger.info("This is an info.");
        logger.warn("This is a warning.");
        logger.error("This is an error.");
        logger.fatal("This is a fatal error.");

        return;
    }
}
