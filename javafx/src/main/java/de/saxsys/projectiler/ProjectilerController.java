package de.saxsys.projectiler;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.Animation.Status;
import javafx.animation.FadeTransitionBuilder;
import javafx.animation.TranslateTransition;
import javafx.animation.TranslateTransitionBuilder;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class ProjectilerController {

    private static final int TRESHOLD = 60;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

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
    void initialize() {
        assert cardImage != null : "fx:id=\"cardImage\" was not injected: check your FXML file 'Projectiler.fxml'.";
        assert timeImage != null : "fx:id=\"timeImage\" was not injected: check your FXML file 'Projectiler.fxml'.";
        assert timeLabel != null : "fx:id=\"timeLabel\" was not injected: check your FXML file 'Projectiler.fxml'.";
        transition =
                TranslateTransitionBuilder.create().node(cardImage).rate(1.5).toY(cardImage.getLayoutY() + 120)
                        .autoReverse(true).cycleCount(2).build();

        createListeners();
        projectChooser.setItems(FXCollections.<String> observableArrayList());
        getProjects();
    }

    private void createListeners() {
        cardImage.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(final MouseEvent arg0) {
                if (!(transition.getStatus() == Status.RUNNING)) {
                    transition.currentTimeProperty().addListener(new ChangeListener<Duration>() {
                        @Override
                        public void changed(final ObservableValue<? extends Duration> arg0, final Duration arg1,
                                final Duration arg2) {
                            if (arg2.greaterThanOrEqualTo(transition.getTotalDuration().divide(2))) {
                                transition.pause();
                                transition.currentTimeProperty().removeListener(this);
                                FadeTransitionBuilder.create().node(cardImage).toValue(0.8).build().play();
                                cardImage.getScene().getRoot().setMouseTransparent(true);
                            }
                        }
                    });
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
                        FadeTransitionBuilder.create().node(cardImage).toValue(1.0).build().play();
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
        System.out.println("getProjects");
        projectilerTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(final WorkerStateEvent t) {
                System.out.println("handle projects");
                projectChooser.setItems(FXCollections.observableArrayList(projectilerTask.valueProperty().get()));
            }
        });
        new Thread(projectilerTask).start();
    }
}
