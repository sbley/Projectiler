package de.saxsys.projectiler.misc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.concurrent.Task;
import de.saxsys.projectiler.Projectiler;

public class ProjectTask extends Task<List<String>> {

    private static final Logger LOGGER = Logger.getLogger(ProjectTask.class.getSimpleName());
    private final Projectiler projectiler;

    public ProjectTask(final Projectiler projectiler) {
        this.projectiler = projectiler;
    }

    @Override
    protected List<String> call() throws Exception {
        List<String> projectNames = Collections.emptyList();
        try {
            projectNames = projectiler.getProjectNames();
        } catch (final Exception e) {
            e.printStackTrace();
            this.succeeded();
            logError("Error during project fetching.", e);
            return new ArrayList<>();
        }
        this.succeeded();
        return projectNames;
    }

    private void logError(String error, Exception e) {
        LOGGER.log(Level.SEVERE, error, e);
        Notification.Notifier.INSTANCE.notifyError("Fehler beim Login", error);
    }

}
