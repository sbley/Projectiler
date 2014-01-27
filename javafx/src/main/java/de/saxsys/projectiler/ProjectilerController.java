package de.saxsys.projectiler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import javafx.animation.Animation.Status;
import javafx.animation.FadeTransitionBuilder;
import javafx.animation.TranslateTransition;
import javafx.animation.TranslateTransitionBuilder;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
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
import de.saxsys.projectiler.concurrent.CheckInTask;
import de.saxsys.projectiler.concurrent.CheckOutTask;
import de.saxsys.projectiler.concurrent.TimeSpentCountUpThread;
import de.saxsys.projectiler.login.Login;
import de.saxsys.projectiler.misc.DisableSceneOnHalfAnimation;
import de.saxsys.projectiler.misc.ProjectTask;
import de.saxsys.projectiler.misc.UITools;

public class ProjectilerController {

    private static final Logger LOGGER = Logger.getLogger(ProjectilerController.class.getSimpleName());

    @FXML
    private StackPane root, timePane;

    private Login login;

    @FXML
    private ImageView timeImage, closeImage;

    @FXML
    private Label fromTimeLabel, toTimeLabel, timeSpentLabel;

    @FXML
    private ChoiceBox<String> projectChooser;

    private TranslateTransition transition;

    private final Projectiler projectiler = Projectiler.createDefaultProjectiler();

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
        login.setVisible(false);
        UITools.makeVisible(closeImage);
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
                    bookProjectAgainstProjectile();
                } else if (liftCardUp()) {
                    bookProjectAgainstProjectile();
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
        Platform.exit();
    }

    private void fillDropDownWithProjects() {
        final ProjectTask projectilerTask = new ProjectTask(projectiler);
        projectilerTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(final WorkerStateEvent t) {
                projectChooser.getItems().clear();
                projectChooser.getItems().addAll(
                        FXCollections.observableArrayList(projectilerTask.valueProperty().get()));
                projectChooser.getSelectionModel().select(0);
                UITools.fadeIn(projectChooser);
            }

        });
        new Thread(projectilerTask).start();
    }

    private void bookProjectAgainstProjectile() {
        Task<?> task = null;
        // if this is the first pull --> checkin
        if (!projectiler.isCheckedIn()) {
            task = new CheckInTask(projectiler);
            ((CheckInTask) task).valueProperty().addListener(new ChangeListener<Date>() {
                @Override
                public void changed(final ObservableValue<? extends Date> bean, final Date oldDate, final Date newDate) {
                    displayFromTimeLabel(newDate);
                    startTimeSpentCountUp(newDate);
                }
            });
            timeSpentLabel.setOpacity(0.2);
            UITools.fadeIn(timeSpentLabel);
        } else {
            // if this is the second pull --> checkout
            final String projectKey = projectChooser.getSelectionModel().getSelectedItem();
            if (projectKey.isEmpty()) {
                return;
            }
            task = new CheckOutTask(projectiler, projectKey);
            loadingIndication(true);
            ((CheckOutTask) task).valueProperty().addListener(new ChangeListener<Date>() {
                @Override
                public void changed(final ObservableValue<? extends Date> bean, final Date oldDate, final Date newDate) {
                    displayToTimeLabel(newDate);
                    loadingIndication(false);
                }
            });
        }
        new Thread(task).start();
    }

    private void loadingIndication(final boolean enabled) {
        double opacity = 1.0;
        if (enabled) {
            opacity = 0.8;
        }
        final Parent root = timeImage.getScene().getRoot();
        root.setMouseTransparent(enabled);
        FadeTransitionBuilder.create().toValue(opacity).duration(Duration.seconds(0.5)).node(root).build().play();
    }

    private void startTimeSpentCountUp(final Date date) {
        new TimeSpentCountUpThread(timeSpentLabel.textProperty(), date).start();
    }

}
