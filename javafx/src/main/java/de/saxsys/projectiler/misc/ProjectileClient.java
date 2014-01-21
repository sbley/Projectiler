package de.saxsys.projectiler.misc;

import java.util.List;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Worker.State;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

public class ProjectileClient {

    public ReadOnlyObjectProperty<State> fillListWithProjects(final List<String> projectList) {
        final ProjectTask projectilerTask = new ProjectTask();
        projectilerTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(final WorkerStateEvent t) {
                projectList.clear();
                projectList.addAll(FXCollections.observableArrayList(projectilerTask.valueProperty().get()));
            }
        });
        new Thread(projectilerTask).start();

        return projectilerTask.stateProperty();
    }

}
