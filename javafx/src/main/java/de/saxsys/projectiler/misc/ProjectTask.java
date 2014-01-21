package de.saxsys.projectiler.misc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.concurrent.Task;
import de.saxsys.projectiler.Projectiler;

public class ProjectTask extends Task<List<String>> {

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
            return new ArrayList<>();
        }
        this.succeeded();
        return projectNames;
    }

}
