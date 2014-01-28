package de.saxsys.projectiler.concurrent;

import java.util.Date;
import java.util.logging.Logger;

import javafx.concurrent.Task;
import de.saxsys.projectiler.Projectiler;

public class CheckInTask extends Task<Date> {

    private static final Logger LOGGER = Logger.getLogger(CheckInTask.class.getSimpleName());

    private final Projectiler projectiler;

    public CheckInTask(final Projectiler projectiler) {
        this.projectiler = projectiler;
    }

    @Override
    protected Date call() {
        LOGGER.info("Perform checkin from GUI");
        Date checkin = null;
        checkin = projectiler.checkin();
        this.succeeded();
        return checkin;
    }
}
