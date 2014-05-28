package de.saxsys.android.projectiler.app.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by stefan.heinze on 28.05.2014.
 */
public class FeatureUtils {

    private static final String FEATURE_NOTIFICATION = "feature_notification";

    public static void featureNotificationSeen(final Context context){
        final SharedPreferences mySharedPreferences = getDefaultSharedPreferences(context);

        final SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putBoolean(FEATURE_NOTIFICATION, true);
        editor.commit();

    }

    public static boolean isFeatureNotificationSeen(final Context context) {
        final SharedPreferences mySharedPreferences = getDefaultSharedPreferences(context);
        return mySharedPreferences.getBoolean(FEATURE_NOTIFICATION, false);
    }


    private static SharedPreferences getDefaultSharedPreferences(final Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

}
