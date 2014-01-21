package de.saxsys.projectiler;

import java.util.logging.Logger;

import javafx.animation.Animation.Status;
import javafx.animation.FadeTransitionBuilder;
import javafx.animation.TranslateTransition;
import javafx.animation.TranslateTransitionBuilder;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import de.saxsys.projectiler.misc.CheckInTask;
import de.saxsys.projectiler.misc.CheckOutTask;
import de.saxsys.projectiler.misc.DisableSceneOnHalfAnimation;
import de.saxsys.projectiler.misc.ProjectTask;

public class ProjectilerController {

    private static final Logger LOGGER = Logger.getLogger(ProjectilerController.class.getSimpleName());

    @FXML
    private StackPane root, timePane;

    @FXML
    private VBox loginVbox;

    @FXML
    private ImageView timeImage;

    @FXML
    private Label fromTimeLabel, toTimeLabel;

    @FXML
    private ChoiceBox<String> projectChooser;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    private TranslateTransition transition;

    private final Projectiler projectiler = Projectiler.createDefaultProjectiler();

    @FXML
    void initialize() {
        initTransition();
        initTextFields();
        initProjectChooser();
        createListeners();
        login();
    }

    /**
     * 
     */
    private void initTransition() {
        transition =
                TranslateTransitionBuilder.create().node(timePane).rate(1.5).toY(timePane.getLayoutY() + 120)
                        .autoReverse(true).cycleCount(2).build();
    }

    /**
     * 
     */
    private void initProjectChooser() {
        projectChooser.setItems(FXCollections
                .<String> observableArrayList("No project currently laoded - maybe an error occured."));
        projectChooser.layout();
    }

    /**
     * 
     */
    private void initTextFields() {
        usernameField.setText(UserDataStore.getInstance().getUserName());
        if (usernameField.textProperty().greaterThan("").get()) {
            passwordField.requestFocus();
        }
    }

    /*
     * GUI STATES
     */
    private void login() {
        loginVbox.setVisible(true);
        hide(projectChooser, timePane, timeImage);
        enable(passwordField, usernameField);
        timePane.setMouseTransparent(true);

        loginButton.disableProperty().bind(
                passwordField.textProperty().greaterThan("").not()
                        .or(usernameField.textProperty().greaterThan("").not()));
    }

    private void loginPending() {
        loginButton.disableProperty().unbind();
        disable(passwordField, usernameField, loginButton);
    }

    private void loggedIn() {
        loginVbox.setVisible(false);
        fadeIn(timePane, projectChooser, timeImage);
        timePane.setMouseTransparent(false);
        root.getChildren().remove(loginVbox);
    }

    /**
     * @return if the card could be pulled down
     */
    private boolean pullCardDown() {
        if (!(transition.getStatus() == Status.RUNNING || transition.getStatus() == Status.PAUSED)) {
            transition.currentTimeProperty().addListener(
                    new DisableSceneOnHalfAnimation(transition, timePane.getScene()));
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

    @FXML
    void onLoginButtonPressed(final ActionEvent event) {
        storeUserData();
        fillDropDownWithProjects();
        // Pending state
        loginPending();
    }

    @FXML
    void onCloseAction(final ActionEvent event) {
        Platform.exit();
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
                if (projectilerTask.valueProperty().get().size() > 0) {
                    loggedIn();
                    storeUserData();
                } else {
                    login();
                }
                projectChooser.getSelectionModel().select(0);
            }

        });
        new Thread(projectilerTask).start();
    }

    private void bookProjectAgainstProjectile() {
        Task<?> task = null;
        // if this is the first pull --> checkin
        if (!projectiler.isCheckedIn()) {
            task = new CheckInTask(projectiler);
        } else {
            // if this is the second pull --> checkout
            final String projectKey = projectChooser.getSelectionModel().getSelectedItem();
            if (projectKey.isEmpty()) {
                return;
            }
            task = new CheckOutTask(projectiler, projectKey);
        }
        new Thread(task).start();
    }

    /**
     * 
     */
    private void storeUserData() {
        final String username = usernameField.getText();
        final String password = passwordField.getText();
        LOGGER.info("Stored user data " + username);
        final UserDataStore userData = UserDataStore.getInstance();
        userData.setCredentials(username, password);
        userData.save();
    }

    private void hide(final Node... nodes) {
        for (final Node node : nodes) {
            node.setOpacity(0.0);
        }
    }

    private void disable(final Node... nodes) {
        for (final Node node : nodes) {
            node.setDisable(true);
        }
    }

    private void enable(final Node... nodes) {
        for (final Node node : nodes) {
            node.setDisable(false);
        }
    }

    private void fadeIn(final Node... nodes) {
        for (final Node node : nodes) {
            final FadeTransitionBuilder build = FadeTransitionBuilder.create().toValue(1.0);
            build.node(node).build().play();
        }
    }

}
