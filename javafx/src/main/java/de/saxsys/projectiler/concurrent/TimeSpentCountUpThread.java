package de.saxsys.projectiler.concurrent;

import java.util.Date;

import javafx.application.Platform;
import javafx.beans.property.StringProperty;

import org.apache.commons.lang3.time.DurationFormatUtils;

public class TimeSpentCountUpThread extends Thread {

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
                e.printStackTrace();
            }
        }
    }
}
