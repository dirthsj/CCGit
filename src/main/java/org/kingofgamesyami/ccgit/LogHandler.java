package org.kingofgamesyami.ccgit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Shared logger implementation
 */
public interface LogHandler {
    /**
     * Print an info message to the console
     *
     * @param message The message to print
     */
    void info(String message);

    /**
     * Print a warning message to the console
     *
     * @param message The message to print
     */
    void warning(String message);

    /**
     * Logger which runs under Minecraft, using the logger provided by {@link LogManager}.
     */
    class FMLLogger implements LogHandler {
        private final Logger logger = LogManager.getLogger("CCGit");

        @Override
        public void info(String message) {
            logger.info(message);
        }

        @Override
        public void warning(String message) {
            logger.warn(message);
        }
    }

    /**
     * Logger which runs in a environment without Minecraft (such as an emulator).
     */
    class BasicLogger implements LogHandler {
        @Override
        public void info(String message) {
            System.out.println("[INFO] [CCGit] " + message);
        }

        @Override
        public void warning(String message) {
            System.out.println("[WARN] [CCGit] " + message);
        }
    }
}
