package de.saxsys.projectiler.concurrent;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.beans.property.StringProperty;

import org.apache.commons.lang3.time.DurationFormatUtils;

public class TimeSpentCountUpThread extends Thread {

    private static final Logger LOGGER = Logger.getLogger(TimeSpentCountUpThread.class.getSimpleName());

    private final StringProperty text;
    private final Date from;

    public TimeSpentCountUpThread(final StringProperty text, final Date from) {
        this.text = text;
        this.from = from;
    }

    @Override
    public void run() {

        while (!isInterrupted()) {
            final long millis = (new Date().getTime() - from.getTime());
            final String formatDuration = DurationFormatUtils.formatDuration(millis, "HH:mm");
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    text.set(formatDuration);
                }
            });
            try {
                Thread.sleep(1000);
            } catch (final InterruptedException e) {
                LOGGER.log(Level.INFO, "Interrupted the count up.");
                // If an interrupt occures while Thread is in wait state, the interrupt flag is not set. We have to
                // set it afterwards
                interrupt();
            }
        }
    }
}
