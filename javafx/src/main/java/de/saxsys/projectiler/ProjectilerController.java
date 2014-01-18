package de.saxsys.projectiler;

import javafx.animation.Animation.Status;
import javafx.animation.FadeTransitionBuilder;
import javafx.animation.TranslateTransition;
import javafx.animation.TranslateTransitionBuilder;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import de.saxsys.projectiler.worker.ClockTask;
import de.saxsys.projectiler.worker.ProjectTask;

public class ProjectilerController {

    @FXML
    private ImageView cardImage;

    @FXML
    private ImageView timeImage;

    @FXML
    private Label timeLabel;

    @FXML
    private ChoiceBox<String> projectChooser;

    @FXML
    private StackPane root;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;
    private TranslateTransition transition;

    @FXML
    private Button loginButton;

    @FXML
    void initialize() {
        assert cardImage != null : "fx:id=\"cardImage\" was not injected: check your FXML file 'Projectiler.fxml'.";
        assert timeImage != null : "fx:id=\"timeImage\" was not injected: check your FXML file 'Projectiler.fxml'.";
        assert timeLabel != null : "fx:id=\"timeLabel\" was not injected: check your FXML file 'Projectiler.fxml'.";
        transition =
                TranslateTransitionBuilder.create().node(cardImage).rate(1.5).toY(cardImage.getLayoutY() + 120)
                        .autoReverse(true).cycleCount(2).build();

        initTextFields();
        createListeners();
        enableLogin();
        initProjectChooser();

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

    private void enableLogin() {
        projectChooser.setOpacity(0.0);
        cardImage.setOpacity(0.0);
        timeImage.setOpacity(0.0);
        passwordField.setDisable(false);
        usernameField.setDisable(false);
        loginButton.disableProperty().bind(
                passwordField.textProperty().greaterThan("").not()
                        .or(usernameField.textProperty().greaterThan("").not()));
        cardImage.setMouseTransparent(true);
    }

    private void disableLogin() {
        FadeTransitionBuilder.create().node(cardImage).toValue(1.0).build().play();
        FadeTransitionBuilder.create().node(projectChooser).toValue(1.0).build().play();
        FadeTransitionBuilder.create().node(timeImage).toValue(1.0).build().play();

        cardImage.setMouseTransparent(false);
        final VBox parent = (VBox) passwordField.getParent();
        ((StackPane) parent.getParent()).getChildren().remove(parent);
    }

    @FXML
    void onLoginButtonPressed(final ActionEvent event) {

        final UserDataStore loadUserData = UserDataStore.getInstance();
        loadUserData.setUserName(usernameField.getText());
        loadUserData.setPassword(passwordField.getText());
        loadUserData.save();

        getProjects();
        passwordField.setDisable(true);
        usernameField.setDisable(true);
        loginButton.disableProperty().unbind();
        loginButton.setDisable(true);
    }

    @FXML
    void onCloseAction(final ActionEvent event) {
        Platform.exit();
    }

    @FXML
    void onCloseButtonClicked(final Event event) {
        Platform.exit();
    }

    private void createListeners() {
        cardImage.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(final MouseEvent arg0) {
                if (!(transition.getStatus() == Status.RUNNING)) {
                    transition.currentTimeProperty().addListener(createCardIsBottomDurationListener());
                }
                callProjectile();
                transition.play();
            }

            private void callProjectile() {
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
        });
    }

    private void getProjects() {
        final ProjectTask projectilerTask = new ProjectTask();
        projectilerTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(final WorkerStateEvent t) {
                projectChooser.getItems().clear();
                projectChooser.getItems().addAll(
                        FXCollections.observableArrayList(projectilerTask.valueProperty().get()));
                if (projectilerTask.valueProperty().get().size() > 0) {
                    disableLogin();
                    final UserDataStore userData = UserDataStore.getInstance();
                    userData.setUserName(usernameField.getText());
                    userData.setPassword(passwordField.getText());
                } else {
                    enableLogin();
                }
                projectChooser.getSelectionModel().select(0);
            }
        });
        new Thread(projectilerTask).start();
    }

    /*************
     * LISTENERS**
     *************/

    private ChangeListener<Duration> createCardIsBottomDurationListener() {
        return new ChangeListener<Duration>() {
            @Override
            public void changed(final ObservableValue<? extends Duration> arg0, final Duration arg1,
                    final Duration newDuration) {
                if (newDuration.greaterThanOrEqualTo(transition.getTotalDuration().divide(2))) {
                    transition.pause();
                    transition.currentTimeProperty().removeListener(this);
                    cardImage.getScene().getRoot().setMouseTransparent(true);
                }
            }
        };
    }

}
