package de.saxsys.projectiler.tray;

import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.stage.Stage;

import javax.imageio.ImageIO;

public class Tray {

    private static final Logger LOGGER = Logger.getLogger(Tray.class.getSimpleName());

    private static final Tray INSTANCE = new Tray();

    private final SystemTray tray = SystemTray.getSystemTray();
    private TrayIcon trayIcon;
    private PopupMenu popup;
    private Stage stage;

    public Tray() {
        // Otherwise the Application will stop, when the window is closed.
        Platform.setImplicitExit(false);
    }

    public static Tray getInstance() {
        return INSTANCE;
    }

    public void initTrayForStage(final Stage stage) {
        if (SystemTray.isSupported() && trayIcon == null) {
            this.stage = stage;
            initTrayPopup();
            initTrayIcon();
            addIconToTray();
        } else {
            LOGGER.log(Level.SEVERE, "Tray unavailable");
        }

    }

    private void addIconToTray() {
        try {
            tray.add(trayIcon);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Tray unavailable - Error while adding tray icon.");
        }
    }

    private void initTrayPopup() {
        popup = new PopupMenu();
        MenuItem open = new MenuItem("Open");
        MenuItem exit = new MenuItem("Exit");
        ActionListener exitListener = new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent arg0) {
                System.exit(0);
            }
        };
        ActionListener maximizeListener = new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent arg0) {
                showStage();
            }
        };
        popup.add(open);
        popup.add(exit);
        exit.addActionListener(exitListener);
        open.addActionListener(maximizeListener);
    }

    private void initTrayIcon() {
        Image image = null;
        try {
            image = ImageIO.read(getClass().getResource("/projectiler.png"));
        } catch (IOException e1) {
            LOGGER.log(Level.SEVERE, "Error loading tray icon", e1);
        }

        trayIcon = new TrayIcon(image, "Projectiler", popup);
        trayIcon.addMouseListener(new ShowStageOnMouseDoubleClickListener(this));
        trayIcon.setImageAutoSize(true);
    }

    public void hideStage() {
        trayIcon.displayMessage("Projectiler", "Ist weiterhin aktiv!", MessageType.INFO);
        LOGGER.log(Level.INFO, "Send Projectiler to tray");
        stage.hide();
    }

    void showStage() {
        LOGGER.log(Level.INFO, "Prepare to open projectiler from tray.");
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                LOGGER.log(Level.INFO, "Projectiler reopened");
                stage.show();
            }
        });
    }
}
