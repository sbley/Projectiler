package de.saxsys.projectiler.concurrent;

import java.util.Date;

import javafx.concurrent.Task;
import de.saxsys.projectiler.Projectiler;

public class CheckInTask extends Task<Date> {

    private final Projectiler projectiler;

    public CheckInTask(final Projectiler projectiler) {
        this.projectiler = projectiler;
    }

    @Override
    protected Date call() throws Exception {
        Date checkin = null;
        try {
            checkin = projectiler.checkin();
        } catch (final Exception e) {
            e.printStackTrace();
        }
        this.succeeded();
        return checkin;
    }
}
