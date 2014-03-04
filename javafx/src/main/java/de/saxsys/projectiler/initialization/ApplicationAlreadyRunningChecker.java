package de.saxsys.projectiler.initialization;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import de.saxsys.projectiler.concurrent.CheckOutTask;

/**
 * Util class which checks, that the application is running once.
 * 
 * @author alexander.casall
 */
public final class ApplicationAlreadyRunningChecker {

    private ApplicationAlreadyRunningChecker() {
    }

    private static final Logger LOGGER = Logger.getLogger(CheckOutTask.class.getSimpleName());

    /**
     * Checks whether the application is running more than one. If this happens, an error dialog is displayed and the
     * application will shut down.
     */
    public static void check() {
        try {
            final BufferedReader reader = createProcessReader();
            int processCount = 0;
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (isProjectilerProcess(line)) {
                        processCount++;
                    }
                }
            } finally {
                reader.close();
                if (processCount > 1) {
                    // Found more then my own process
                    stopStartUp();
                }
            }
        } catch (final IOException e) {
            LOGGER.info("Start Projectiler!");
        }
    }

    private static boolean isProjectilerProcess(String line) {
        return line.toLowerCase().contains("projectiler") && !line.toLowerCase().contains("setup");
    }

    private static BufferedReader createProcessReader() throws IOException {
        final Process proc = Runtime.getRuntime().exec("tasklist /NH");
        final BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        return reader;
    }

    private static void stopStartUp() {
        JOptionPane.showMessageDialog(null, "Projectiler läuft bereits - und ja, der Dialog ist häßlich.", "Error",
                JOptionPane.ERROR_MESSAGE);
        System.exit(0);
    }
}
