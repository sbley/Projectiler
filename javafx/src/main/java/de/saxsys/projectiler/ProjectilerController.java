package de.saxsys.projectiler;

import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.animation.Animation.Status;
import javafx.animation.FadeTransitionBuilder;
import javafx.animation.TranslateTransition;
import javafx.animation.TranslateTransitionBuilder;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import org.apache.commons.lang3.StringUtils;

import de.saxsys.projectiler.concurrent.CheckInTask;
import de.saxsys.projectiler.concurrent.CheckOutTask;
import de.saxsys.projectiler.concurrent.TimeSpentCountUpThread;
import de.saxsys.projectiler.login.Login;
import de.saxsys.projectiler.misc.DisableSceneOnHalfAnimation;
import de.saxsys.projectiler.misc.Notification;
import de.saxsys.projectiler.misc.ProjectTask;
import de.saxsys.projectiler.misc.UITools;
import de.saxsys.projectiler.tray.Tray;

public class ProjectilerController {

    private static final double OPAQUE_VALUE = 0.8;
    private static final double VISIBLE_VALUE = 1.0;

    private Login login;

    @FXML
    private StackPane root, timePane;
    @FXML
    private ImageView timeImage, closeImage;
    @FXML
    private Label fromTimeLabel, toTimeLabel, timeSpentLabel;
    @FXML
    private ChoiceBox<String> projectChooser;

    private TranslateTransition transition;
    private final Projectiler projectiler = Projectiler.createDefaultProjectiler();
    private TimeSpentCountUpThread timeSpentCountUpThread;

    @FXML
    void initialize() {
        initLogin();
        initTransition();
        initProjectChooser();
        createListeners();
        login();
    }

    private void initLogin() {
        login = new Login(projectiler);
        login.loginSucessfulProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(final ObservableValue<? extends Boolean> arg0, final Boolean arg1, final Boolean arg2) {
                loggedIn();
                fillDropDownWithProjects();
                if (projectiler.isCheckedIn()) {
                    Notification.Notifier.INSTANCE.notifySuccess("Bestehende Buchung",
                            "Bestehende Buchung wiederhergestellt.");
                    performCheckIn();
                    pullCardDown();
                }
            }
        });
    }

    /**
     * 
     */
    private void initTransition() {
        transition =
                TranslateTransitionBuilder.create().node(timePane).rate(2).toY(timePane.getLayoutY() + 80)
                        .autoReverse(true).cycleCount(2).build();
    }

    /**
     * 
     */
    private void initProjectChooser() {
        projectChooser.setItems(FXCollections
                .<String> observableArrayList("No project currently laoded - maybe an error occured."));
        projectChooser.setOpacity(0.0);
    }

    /*
     * GUI STATES
     */
    private void login() {
        login.setVisible(true);
        root.getChildren().add(login);
        UITools.makeInvisible(closeImage);
        UITools.hide(projectChooser, timePane, timeImage);
        timePane.setMouseTransparent(true);
    }

    private void loggedIn() {
        loadingIndication(false);
        login.setVisible(false);
        UITools.makeVisible(closeImage);
        UITools.hide(fromTimeLabel, toTimeLabel, timeSpentLabel);
        UITools.fadeIn(timePane, timeImage);
        timePane.setMouseTransparent(false);
        root.getChildren().remove(login);
    }

    /**
     * @return if the card could be pulled down
     */
    private boolean pullCardDown() {
        if (!(transition.getStatus() == Status.RUNNING || transition.getStatus() == Status.PAUSED)) {
            transition.currentTimeProperty().addListener(new DisableSceneOnHalfAnimation(transition));
            transition.play();
            return true;
        }
        return false;
    }

    /**
     * @return
     * 
     */
    private boolean liftCardUp() {
        if (transition.getStatus() == Status.PAUSED) {
            transition.play();
            return true;
        }
        return false;
    }

    /*
     * Button handler
     */

    private void createListeners() {
        timePane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(final MouseEvent arg0) {
                if (pullCardDown()) {
                    performCheckIn();
                } else if (liftCardUp()) {
                    performCheckOut();
                }
            }
        });
    }

    /*
     * Labels
     */
    private void displayFromTimeLabel(final Date date) {
        if (date == null) {
            toTimeLabel.setText("Error");
        } else {
            final String displayString = new SimpleDateFormat("H:mm").format(date);
            fromTimeLabel.setText(displayString);
            fromTimeLabel.setOpacity(0.0);
            UITools.fadeIn(fromTimeLabel);
        }
    }

    /*
     * Labels
     */
    private void displayToTimeLabel(final Date date) {
        // Stop Timer which counted the checkin time
        timeSpentCountUpThread.interrupt();
        if (date == null) {
            toTimeLabel.setText("Error");
        } else {
            final String displayString = new SimpleDateFormat("H:mm").format(date);
            toTimeLabel.setText(displayString);
            toTimeLabel.setOpacity(0.0);
            UITools.fadeIn(toTimeLabel);
        }
    }

    @FXML
    void onCloseButtonClicked(final Event event) {
        Tray.getInstance().hideStage();
    }

    private void fillDropDownWithProjects() {
        final ProjectTask projectilerTask = new ProjectTask(projectiler);
        projectilerTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(final WorkerStateEvent t) {
                projectChooser.getItems().clear();
                projectChooser.getItems().addAll(
                        FXCollections.observableArrayList(projectilerTask.valueProperty().get()));
                projectChooser.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(final ObservableValue<? extends String> arg0, final String arg1,
                            final String arg2) {
                        if (arg2 != null && !arg2.isEmpty()) {
                            projectiler.saveProjectName(arg2);
                        }
                    }
                });
                final String projectName = UserDataStore.getInstance().getProjectName();
                if (projectName == null) {
                    projectChooser.getSelectionModel().select(0);
                } else {
                    projectChooser.getSelectionModel().select(projectName);
                }
                UITools.fadeIn(projectChooser);
            }

        });
        new Thread(projectilerTask).start();
    }

    private void performCheckIn() {
        if (projectiler.isCheckedIn()) {
            // Already checked in
            final Date startDate = UserDataStore.getInstance().getStartDate();
            displayFromTimeLabel(startDate);
            startTimeSpentCountUp(startDate);
        } else {
            final CheckInTask task = new CheckInTask(projectiler);
            task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(final WorkerStateEvent arg0) {
                    if (task.getValue() != null) {
                        displayFromTimeLabel(task.getValue());
                        startTimeSpentCountUp(task.getValue());
                    } else {
                        liftCardUp();
                    }
                }
            });
            new Thread(task).start();
        }
        timeSpentLabel.setOpacity(0.2);
        UITools.fadeIn(timeSpentLabel);

    }

    private void performCheckOut() {
        final String projectKey = projectChooser.getSelectionModel().getSelectedItem();
        if (StringUtils.isEmpty(projectKey)) {
            return;
        }
        final CheckOutTask task = new CheckOutTask(projectiler, projectKey);
        loadingIndication(true);
        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(final WorkerStateEvent event) {
                if (task.getValue() != null) {
                    displayToTimeLabel(task.getValue());
                }
                loadingIndication(false);
            }
        });
        new Thread(task).start();
    }

    private void loadingIndication(final boolean enabled) {
        double opacity = VISIBLE_VALUE;
        if (enabled) {
            opacity = OPAQUE_VALUE;
        }
        final Parent root = timeImage.getScene().getRoot();
        root.setMouseTransparent(enabled);
        FadeTransitionBuilder.create().toValue(opacity).duration(Duration.seconds(0.5)).node(root).build().play();
    }

    private void startTimeSpentCountUp(final Date date) {
        timeSpentCountUpThread = new TimeSpentCountUpThread(timeSpentLabel.textProperty(), date);
        UITools.fadeIn(timeSpentLabel);
        timeSpentCountUpThread.start();
    }

}
