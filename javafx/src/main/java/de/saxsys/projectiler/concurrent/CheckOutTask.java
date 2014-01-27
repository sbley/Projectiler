package de.saxsys.projectiler.concurrent;

import java.util.Date;

import javafx.concurrent.Task;
import de.saxsys.projectiler.Projectiler;

public class CheckOutTask extends Task<Date> {

    private final String projectKey;
    private final Projectiler projectiler;

    public CheckOutTask(final Projectiler projectiler, final String projectKey) {
        this.projectiler = projectiler;
        this.projectKey = projectKey;
    }

    @Override
    protected Date call() throws Exception {
        Date checkout = null;
        try {
            checkout = projectiler.checkout(projectKey);
            projectiler.saveProjectName(projectKey);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        this.succeeded();
        return checkout;
    }
}
