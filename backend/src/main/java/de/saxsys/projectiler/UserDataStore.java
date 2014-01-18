package de.saxsys.projectiler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Date;

public class UserDataStore implements Serializable {

    private static final long serialVersionUID = -8326125819925449250L;

    // Wenn wir wissen wie wir ausliefern -> tauschen
    private static final String FILENAME = "D:///data.projectiler";

    private Date startDate;
    private String userName;

    private UserDataStore() {
    }

    public void save() {
        try {
            final OutputStream file = new FileOutputStream(FILENAME);
            final OutputStream buffer = new BufferedOutputStream(file);
            final ObjectOutput output = new ObjectOutputStream(buffer);
            output.writeObject(this);
            output.flush();
            output.close();
        } catch (final IOException ex) {
            System.err.println("Couldn't write existing profile from disk");
        }
    }

    public static UserDataStore loadUserData() {
        try {
            final InputStream file = new FileInputStream(FILENAME);
            final InputStream buffer = new BufferedInputStream(file);
            final ObjectInput input = new ObjectInputStream(buffer);
            // deserialize the List
            final UserDataStore data = (UserDataStore) input.readObject();
            return data;
        } catch (final Exception e) {
            System.err.println("Couldn't load existing profile from disk");
        }
        return new UserDataStore();
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
}
