package de.saxsys.projectiler.misc;

import javafx.concurrent.Task;
import de.saxsys.projectiler.Projectiler;

public class CheckInTask extends Task<Boolean> {

    private final Projectiler projectiler;

    public CheckInTask(final Projectiler projectiler) {
        this.projectiler = projectiler;
    }

    @Override
    protected Boolean call() throws Exception {
        try {
            projectiler.checkin();
        } catch (final Exception e) {
            e.printStackTrace();
            this.succeeded();
            return false;
        }
        this.succeeded();
        return true;
    }
}
