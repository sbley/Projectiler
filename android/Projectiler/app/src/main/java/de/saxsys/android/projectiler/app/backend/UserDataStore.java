package de.saxsys.android.projectiler.app.backend;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import de.saxsys.android.projectiler.app.db.DataProvider;
import de.saxsys.android.projectiler.app.generatedmodel.Comment;
import de.saxsys.android.projectiler.app.generatedmodel.Track;

public class UserDataStore implements Serializable {

	private static final Logger LOGGER = Logger.getLogger(UserDataStore.class.getSimpleName());
	private static final long serialVersionUID = -8326125819925449250L;


    private static final String USER_NAME = "user_name";
    private static final String PASSWORD = "password";
    private static final String PROJECT_NAME = "project_name";
    private static final String AUTO_LOGIN = "auto_login";
    private static final String START_DATE = "start_date";
    private static final String WIDGET_LOADING = "widget_loading";
    private static final String COMMENT = "comment";
    private static final String PROJECT_NAMES = "project_names";

	private static UserDataStore INSTANCE;

    private DataProvider dataProvider;
    private Context context;

	public static UserDataStore getInstance(final Context context) {
		if (null == INSTANCE) {
			INSTANCE = new UserDataStore(context);
		}
		return INSTANCE;
	}

	private UserDataStore(final Context context) {
        dataProvider = new DataProvider(context);
        this.context = context;
	}

	public void setUserName(final String name) {
        final SharedPreferences sharedPreferences = getDefaultSharedPreferences();

        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_NAME, name);
        editor.commit();
	}

	public void setStartDate(final Date date) {
        final SharedPreferences sharedPreferences = getDefaultSharedPreferences();

        final SharedPreferences.Editor editor = sharedPreferences.edit();

        if(date == null){
            editor.putString(START_DATE, null);
        }else{
            editor.putString(START_DATE, DateUtil.formatShort(date));
        }
        editor.commit();
	}

	public String getUserName() {

        final SharedPreferences mySharedPreferences = getDefaultSharedPreferences();

        return mySharedPreferences.getString(USER_NAME, "");
	}

	public Date getStartDate() {
        final SharedPreferences mySharedPreferences = getDefaultSharedPreferences();

        return DateUtil.formatShort(mySharedPreferences.getString(START_DATE, ""));
	}

    public String getStartDateAsString() {
        final SharedPreferences mySharedPreferences = getDefaultSharedPreferences();

        return mySharedPreferences.getString(START_DATE, "");
    }


	public String getPassword() {
        final SharedPreferences mySharedPreferences = getDefaultSharedPreferences();

        return mySharedPreferences.getString(PASSWORD, "");
	}

	public void setPassword(final String password) {

        final SharedPreferences sharedPreferences = getDefaultSharedPreferences();

        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PASSWORD, password);
        editor.commit();
	}

	public String getProjectName() {
        final SharedPreferences mySharedPreferences = getDefaultSharedPreferences();

        return mySharedPreferences.getString(PROJECT_NAME, "");
	}

	public void setProjectName(final String projectName) {

        final SharedPreferences sharedPreferences = getDefaultSharedPreferences();

        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PROJECT_NAME, projectName);
        editor.commit();

	}

    public void setAutoLogin(final boolean autoLogin){
        final SharedPreferences sharedPreferences = getDefaultSharedPreferences();

        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(AUTO_LOGIN, autoLogin);
        editor.commit();
    }

    public boolean getAutoLogin(){
        final SharedPreferences mySharedPreferences = getDefaultSharedPreferences();

        return mySharedPreferences.getBoolean(AUTO_LOGIN, false);
    }

	/*
	 * Helper
	 */
	public void setCredentials(final String userName, final String password) {
		this.setUserName(userName);
		this.setPassword(password);
	}

    public int getCurrentActiveProjectIndex(final List<String> items){

        int ret = -1;

        if(getStartDate() == null){
            return -1;
        }

        String selectedProject = getProjectName();

        for(int i = 0; i < items.size(); i++){
            if(items.get(i).equals(selectedProject)){
                return i;
            }
        }

        return ret;
    }

	public void clearStartDate() {
		setStartDate(null);
	}

	public boolean isCheckedIn() {
		return null != getStartDate();
	}

    private SharedPreferences getDefaultSharedPreferences() {
        return context.getSharedPreferences("projectiler", Context.MODE_PRIVATE);
    }

    public void setWidgetLoading(boolean loading) {
        final SharedPreferences sharedPreferences = getDefaultSharedPreferences();

        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(WIDGET_LOADING, loading);

        editor.commit();
    }

    public boolean isWidgetLoading(){
        final SharedPreferences mySharedPreferences = getDefaultSharedPreferences();

        return mySharedPreferences.getBoolean(WIDGET_LOADING, false);
    }

    public void saveComment(String comment) {
        final SharedPreferences sharedPreferences = getDefaultSharedPreferences();

        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(COMMENT, comment);
        editor.commit();

    }

    public void deleteComment(){
        persistComment();
        final SharedPreferences sharedPreferences = getDefaultSharedPreferences();

        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(COMMENT, "");
        editor.commit();
    }

    public String getComment() {
        final SharedPreferences mySharedPreferences = getDefaultSharedPreferences();

        return mySharedPreferences.getString(COMMENT, "");
    }

    public void saveTrack(Track track) {
        dataProvider.saveTrack(track);
    }

    public List<Track> getTracks() {
        return dataProvider.getTracks();
    }

    public void deleteTrack(Track track) {
        dataProvider.deleteTrack(track);
    }

    public void persistComment(){

        if(!getComment().equals("")){
            Comment comment = new Comment();
            comment.setTimestamp(new Date());
            comment.setValue(getComment());

            dataProvider.saveComment(comment);
        }
    }

    public List<Comment> searchComments(String charSequence) {
        return dataProvider.searchComments(charSequence);
    }

    public List<String> getProjectNames() {
        final SharedPreferences sharedPreferences = getDefaultSharedPreferences();
        String[] mylist = TextUtils.split(sharedPreferences.getString(PROJECT_NAMES, ""), "‚‗‚");
        ArrayList<String> gottenlist = new ArrayList<String>(Arrays.asList(mylist));
        return gottenlist;
    }

    public void savePorjectNames(List<String> projectNames) {
        final SharedPreferences sharedPreferences = getDefaultSharedPreferences();
        String[] projectNamesStringArray = projectNames.toArray(new String[projectNames.size()]);

        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PROJECT_NAMES, TextUtils.join("‚‗‚", projectNamesStringArray));
        editor.commit();
    }
}
