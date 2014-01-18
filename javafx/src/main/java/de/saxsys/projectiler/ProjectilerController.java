package de.saxsys.projectiler;

import javafx.animation.Animation.Status;
import javafx.animation.FadeTransitionBuilder;
import javafx.animation.TranslateTransition;
import javafx.animation.TranslateTransitionBuilder;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
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

        enableLogin();

        createListeners();
        projectChooser.setItems(FXCollections
                .<String> observableArrayList("No project currently laoded - maybe an error occured."));
        projectChooser.layout();
    }

    @FXML
    void onLoginButtonPressed(final ActionEvent event) {
        getProjects();
        passwordField.setDisable(true);
        usernameField.setDisable(true);
        loginButton.disableProperty().unbind();
        loginButton.setDisable(true);
    }

    private void enableLogin() {
        projectChooser.setOpacity(0.0);
        cardImage.setOpacity(0.0);
        timeImage.setOpacity(0.0);
        loginButton.disableProperty().bind(
                passwordField.textProperty().greaterThan("").not()
                        .or(usernameField.textProperty().greaterThan("").not()));
        cardImage.setMouseTransparent(true);
    }

    private void disableLogin() {
        FadeTransitionBuilder.create().node(cardImage).toValue(1.0).build().play();
        FadeTransitionBuilder.create().node(timeImage).toValue(1.0).onFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent arg0) {
                FadeTransitionBuilder.create().node(projectChooser).toValue(1.0).build().play();
            }
        }).build().play();

        cardImage.setMouseTransparent(false);
        final VBox parent = (VBox) passwordField.getParent();
        ((StackPane) parent.getParent()).getChildren().remove(parent);
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
                final ClockTask projectilerTask =
                        new ClockTask(usernameField.getText(), usernameField.getText(), selectedItem);
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
        final ProjectTask projectilerTask = new ProjectTask(usernameField.getText(), passwordField.getText());
        projectilerTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(final WorkerStateEvent t) {
                projectChooser.getItems().clear();
                projectChooser.getItems().addAll(
                        FXCollections.observableArrayList(projectilerTask.valueProperty().get()));
                disableLogin();
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
                    FadeTransitionBuilder.create().node(cardImage).toValue(0.8).build().play();
                    cardImage.getScene().getRoot().setMouseTransparent(true);
                }
            }
        };
    }

}
