package de.saxsys.android.projectiler.app.backend;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class UserDataStore implements Serializable {

	private static final Logger LOGGER = Logger.getLogger(UserDataStore.class.getSimpleName());
	private static final long serialVersionUID = -8326125819925449250L;


    private static final String USER_NAME = "user_name";
    private static final String PASSWORD = "password";
    private static final String PROJECT_NAME = "project_name";
    private static final String AUTO_LOGIN = "auto_login";
    private static final String START_DATE = "start_date";
    private static final String WIDGET_LOADING = "widget_loading";

	private static UserDataStore INSTANCE;

	public static UserDataStore getInstance() {
		if (null == INSTANCE) {
			INSTANCE = new UserDataStore();
		}
		return INSTANCE;
	}

	private UserDataStore() {
	}

	public void setUserName(final Context context, final String name) {
        final SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);

        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_NAME, name);
        editor.commit();
	}

	public void setStartDate(final Context context, final Date date) {
        final SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);

        final SharedPreferences.Editor editor = sharedPreferences.edit();

        if(date == null){
            editor.putString(START_DATE, null);
        }else{
            editor.putString(START_DATE, DateUtil.formatShort(date));
        }
        editor.commit();
	}

	public String getUserName(final Context context) {

        final SharedPreferences mySharedPreferences = getDefaultSharedPreferences(context);

        return mySharedPreferences.getString(USER_NAME, "");
	}

	public Date getStartDate(final Context context) {
        final SharedPreferences mySharedPreferences = getDefaultSharedPreferences(context);

        return DateUtil.formatShort(mySharedPreferences.getString(START_DATE, ""));
	}

    public String getStartDateAsString(final Context context) {
        final SharedPreferences mySharedPreferences = getDefaultSharedPreferences(context);

        return mySharedPreferences.getString(START_DATE, "");
    }


	public String getPassword(final Context context) {
        final SharedPreferences mySharedPreferences = getDefaultSharedPreferences(context);

        return mySharedPreferences.getString(PASSWORD, "");
	}

	public void setPassword(final Context context, final String password) {

        final SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);

        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PASSWORD, password);
        editor.commit();
	}

	public String getProjectName(final Context context) {
        final SharedPreferences mySharedPreferences = getDefaultSharedPreferences(context);

        return mySharedPreferences.getString(PROJECT_NAME, "");
	}

	public void setProjectName(final Context context, final String projectName) {

        final SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);

        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PROJECT_NAME, projectName);
        editor.commit();

	}

    public void setAutoLogin(final Context context, final boolean autoLogin){
        final SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);

        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(AUTO_LOGIN, autoLogin);
        editor.commit();
    }

    public boolean getAutoLogin(final Context context){
        final SharedPreferences mySharedPreferences = getDefaultSharedPreferences(context);

        return mySharedPreferences.getBoolean(AUTO_LOGIN, false);
    }

	/*
	 * Helper
	 */
	public void setCredentials(final Context context, final String userName, final String password) {
		this.setUserName(context, userName);
		this.setPassword(context, password);
	}

    public int getCurrentActiveIndex(final Context context, final List<String> items){

        int ret = -1;

        if(getStartDate(context) == null){
            return -1;
        }

        String selectedProject = getProjectName(context);

        if(selectedProject.equals("")){
            return ret;
        }

        for(int i = 0; i < items.size(); i++){
            if(items.get(i).equals(selectedProject)){
                return i;
            }
        }

        return ret;
    }

	public void clearStartDate(final Context context) {
		setStartDate(context, null);
	}

	public boolean isCheckedIn(final Context context) {
		return null != getStartDate(context);
	}

    private static SharedPreferences getDefaultSharedPreferences(final Context context) {
        return context.getSharedPreferences("projectiler", Context.MODE_PRIVATE);
    }

    public void setWidgetLoading(Context context, boolean loading) {
        final SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);

        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(WIDGET_LOADING, loading);

        editor.commit();
    }

    public boolean isWidgetLoading(final Context context){
        final SharedPreferences mySharedPreferences = getDefaultSharedPreferences(context);

        return mySharedPreferences.getBoolean(WIDGET_LOADING, false);
    }
}
