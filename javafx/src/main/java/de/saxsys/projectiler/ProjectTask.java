package de.saxsys.projectiler;

import java.util.ArrayList;
import java.util.List;

import javafx.concurrent.Task;
import de.saxsys.projectiler.domain.Password;
import de.saxsys.projectiler.domain.User;
import de.saxsys.projectiler.selenium.SeleniumCrawler;
import de.saxsys.projectiler.selenium.Settings;

public class ProjectTask extends Task<List<String>> {

    @Override
    protected List<String> call() throws Exception {
        Projectiler projectiler = null;
        try {
            projectiler =
                    new Projectiler(new User("alexander.casall", Password.get()), new SeleniumCrawler(new Settings()));
        } catch (final Exception e) {
            e.printStackTrace();
            this.succeeded();
            return new ArrayList<>();
        }
        this.succeeded();
        return projectiler.getProjectNames();
    }

}
