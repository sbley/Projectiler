package de.saxsys.android.projectiler.app.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by stefan.heinze on 26.05.2014.
 */
public class SettingsUtils {

    public static boolean isSendNotification(final Context context) {
        final SharedPreferences mySharedPreferences = getDefaultSharedPreferences(context);
        return mySharedPreferences.getBoolean("notifications_new_message", false);
    }

    public static boolean isVibration(final Context context) {
        final SharedPreferences mySharedPreferences = getDefaultSharedPreferences(context);
        return mySharedPreferences.getBoolean("notifications_new_message_vibrate", false);
    }

    public static String startWork(final Context context) {
        final SharedPreferences mySharedPreferences = getDefaultSharedPreferences(context);
        return mySharedPreferences.getString("work_begin", "08:00");
    }

    public static String endWork(final Context context) {
        final SharedPreferences mySharedPreferences = getDefaultSharedPreferences(context);
        return mySharedPreferences.getString("work_end", "17:00");
    }

    public static String startBreak(final Context context) {
        final SharedPreferences mySharedPreferences = getDefaultSharedPreferences(context);
        return mySharedPreferences.getString("break_start", "12:00");
    }

    public static String endBreak(final Context context) {
        final SharedPreferences mySharedPreferences = getDefaultSharedPreferences(context);
        return mySharedPreferences.getString("break_end", "13:00");
    }

    private static SharedPreferences getDefaultSharedPreferences(final Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

}
