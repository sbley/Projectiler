package de.saxsys.projectiler.initialization;

import javafx.scene.text.Font;
import de.saxsys.projectiler.ClientStarter;

public class ProjectilerFonts {
    public static void initFonts() {
        Font.loadFont(ClientStarter.class.getResource("/segoeuil.ttf").toExternalForm(), 10);
    }
}
