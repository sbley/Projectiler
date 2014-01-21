package de.saxsys.projectiler;

import javafx.animation.Animation.Status;
import javafx.animation.FadeTransitionBuilder;
import javafx.animation.TranslateTransition;
import javafx.animation.TranslateTransitionBuilder;
import javafx.application.Platform;
import javafx.collections.FXCollections;
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
import de.saxsys.projectiler.misc.ClockTask;
import de.saxsys.projectiler.misc.DisableSceneOnHalfAnimation;
import de.saxsys.projectiler.misc.ProjectTask;

public class ProjectilerController {

    @FXML
    private StackPane root;

    @FXML
    private VBox loginVbox;

    @FXML
    private ImageView cardImage, timeImage;

    @FXML
    private Label timeLabel;

    @FXML
    private ChoiceBox<String> projectChooser;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    private TranslateTransition transition;

    @FXML
    void initialize() {
        assert cardImage != null : "fx:id=\"cardImage\" was not injected: check your FXML file 'Projectiler.fxml'.";
        assert timeImage != null : "fx:id=\"timeImage\" was not injected: check your FXML file 'Projectiler.fxml'.";
        assert timeLabel != null : "fx:id=\"timeLabel\" was not injected: check your FXML file 'Projectiler.fxml'.";
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
                TranslateTransitionBuilder.create().node(cardImage).rate(1.5).toY(cardImage.getLayoutY() + 120)
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
        hide(projectChooser, cardImage, timeImage);
        enable(passwordField, usernameField);
        cardImage.setMouseTransparent(true);

        loginButton.disableProperty().bind(
                passwordField.textProperty().greaterThan("").not()
                        .or(usernameField.textProperty().greaterThan("").not()));
    }

    private void loginPending() {
        loginButton.disableProperty().unbind();
        disable(passwordField, usernameField, loginButton);
    }

    private void loggedIn() {
        fadeIn(cardImage, projectChooser, timeImage);
        cardImage.setMouseTransparent(false);
        root.getChildren().remove(loginVbox);
    }

    /*
     * Button handler
     */

    private void createListeners() {
        cardImage.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(final MouseEvent arg0) {
                if (!(transition.getStatus() == Status.RUNNING)) {
                    transition.currentTimeProperty().addListener(
                            new DisableSceneOnHalfAnimation(transition, cardImage.getScene()));
                    bookProjectAgainstProjectile();
                }
                transition.play();
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
        final ProjectTask projectilerTask = new ProjectTask();
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
        final String selectedItem = projectChooser.getSelectionModel().getSelectedItem();
        if (selectedItem.isEmpty()) {
            return;
        }
        final ClockTask projectilerTask = new ClockTask(selectedItem);
        projectilerTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(final WorkerStateEvent t) {
                transition.play();
                cardImage.getScene().getRoot().setMouseTransparent(false);
            }
        });
        new Thread(projectilerTask).start();
    }

    /**
     * 
     */
    private void storeUserData() {
        final UserDataStore userData = UserDataStore.getInstance();
        userData.setUserName(usernameField.getText());
        userData.setPassword(passwordField.getText());
        userData.save();
    }

    private void show(final Node... nodes) {
        for (final Node node : nodes) {
            node.setOpacity(1.0);
        }
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
