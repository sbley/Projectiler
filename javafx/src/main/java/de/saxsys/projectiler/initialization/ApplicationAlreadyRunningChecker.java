package de.saxsys.projectiler.initialization;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import de.saxsys.projectiler.concurrent.CheckOutTask;

public class ApplicationAlreadyRunningChecker {

    private static final Logger LOGGER = Logger.getLogger(CheckOutTask.class.getSimpleName());

    public static void check() {
        try {
            final Process proc = Runtime.getRuntime().exec("tasklist /NH");
            final BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("Projectiler")) {
                        JOptionPane.showMessageDialog(null,
                                "Projectiler läuft bereits - und ja, der Dialog ist häßlich.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                        System.exit(0);
                    }
                }
            } finally {
                reader.close();
            }
        } catch (final IOException e) {
            LOGGER.info("Start Projectiler!");
        }
    }
}
