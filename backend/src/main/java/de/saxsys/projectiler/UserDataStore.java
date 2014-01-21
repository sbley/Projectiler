package de.saxsys.projectiler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.logging.Logger;

public class UserDataStore implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(UserDataStore.class.getSimpleName());
    private static final long serialVersionUID = -8326125819925449250L;

    // TODO Wenn wir wissen wie wir ausliefern -> tauschen
    private static final String FILENAME = System.getProperty("userdata.file", System.getProperty("user.home")
            + "/.projectiler/data.projectiler");
    private static final Path FILEPATH = Paths.get(FILENAME);

    private Date startDate;
    private String userName;
    private transient String password;

    private static UserDataStore INSTANCE;

    public static UserDataStore getInstance() {
        if (null == INSTANCE) {
            INSTANCE = new UserDataStore();
            INSTANCE.load();
        }
        return INSTANCE;
    }

    private UserDataStore() {
    }

    public void save() {
        try {
            Files.createDirectories(FILEPATH.getParent());
            try (ObjectOutput output =
                    new ObjectOutputStream(new BufferedOutputStream(Files.newOutputStream(FILEPATH)))) {
                output.writeObject(INSTANCE);
            }
        } catch (final IOException e) {
            LOGGER.severe("Couldn't write existing profile to disk. " + e.getMessage());
        }
    }

    public void load() {
        try (ObjectInput input = new ObjectInputStream(new BufferedInputStream(Files.newInputStream(FILEPATH)))) {
            // deserialize the list
            final UserDataStore data = (UserDataStore) input.readObject();
            INSTANCE.setStartDate(data.getStartDate());
            INSTANCE.setUserName(data.getUserName());
        } catch (final Exception e) {
            LOGGER.severe("Couldn't load existing profile from disk. " + e.getMessage());
        }
    }

    public void setUserName(final String name) {
        this.userName = name;
    }

    public void setStartDate(final Date date) {
        this.startDate = date;
    }

    public String getUserName() {
        return userName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public void setCredentials(final String userName, final String password) {
        this.setUserName(userName);
        this.setPassword(password);
    }

    public void clearStartDate() {
        setStartDate(null);
    }
}
