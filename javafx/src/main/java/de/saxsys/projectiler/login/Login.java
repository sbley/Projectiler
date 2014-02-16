package de.saxsys.projectiler.login;

import java.io.IOException;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import de.saxsys.projectiler.Projectiler;
//import de.saxsys.projectiler.UserDataStore;
import de.saxsys.projectiler.concurrent.LoginTask;
import de.saxsys.projectiler.misc.Notification;
import de.saxsys.projectiler.misc.UITools;

public class Login extends VBox {

    private static final Logger LOGGER = Logger.getLogger(Login.class.getSimpleName());

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    private final ReadOnlyBooleanWrapper loginSucessful = new ReadOnlyBooleanWrapper();
    private final ReadOnlyBooleanWrapper loginFailed = new ReadOnlyBooleanWrapper();

    private final Projectiler projectiler;

    public Login(final Projectiler projectiler) {
        this.projectiler = projectiler;
        final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Login.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (final IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @FXML
    void onCloseAction(final ActionEvent event) {
        Platform.exit();
    }

    @FXML
    void onLoginButtonPressed(final ActionEvent event) {
        storeUserData();
    }

    /**
     * 
     */
    private void storeUserData() {
        loginPending();
        final String username = usernameField.getText();
        final String password = passwordField.getText();
        LOGGER.info("Stored user data " + username);
        final LoginTask loginTask = new LoginTask(projectiler, username, password);
        new Thread(loginTask).start();
        loginTask.valueProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(final ObservableValue<? extends Boolean> arg0, final Boolean arg1,
                    final Boolean sucessful) {
                loginSucessful.set(sucessful);
                loginFailed.set(!sucessful);
                loginFinished();
                if (!sucessful) {
                    Notification.Notifier.INSTANCE.notifyError("Fehler beim Login",
                            "Logindaten sind falsch\noder Konto ist gesperrt.");
                }
            }
        });
    }

    @FXML
    void initialize() {
        initTextFields();
        initButtonBinding();
    }

    private void initTextFields() {
        final String userName = projectiler.getUserName();
        usernameField.setText(userName);
        if (!userName.isEmpty()) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    passwordField.requestFocus();
                }
            });
        }
    }

    private void initButtonBinding() {
        loginButton.disableProperty().bind(
                passwordField.textProperty().greaterThan("").not()
                        .or(usernameField.textProperty().greaterThan("").not()));
    }

    private void loginPending() {
        loginButton.disableProperty().unbind();
        UITools.disable(passwordField, usernameField, loginButton);
    }

    private void loginFinished() {
        UITools.enable(passwordField, usernameField, loginButton);
        initButtonBinding();
    }

    public ReadOnlyBooleanProperty loginSucessfulProperty() {
        return loginSucessful.getReadOnlyProperty();
    }

    public ReadOnlyBooleanProperty loginFailedProperty() {
        return loginFailed.getReadOnlyProperty();
    }

    public boolean getLoginSucessful() {
        return loginSucessful.get();
    }

    public boolean getLoginFailed() {
        return loginFailed.get();
    }

}
