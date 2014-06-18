package de.saxsys.android.projectiler.app.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import de.saxsys.android.projectiler.app.backend.UserDataStore;
import de.saxsys.android.projectiler.app.receiver.ProjectilerBroadcastReceiver;

/**
 * Created by stefan.heinze on 18.06.2014.
 */
public class IntentUtils {


    public static PendingIntent createStartPendingIntent(final Context context){
        Intent startIntent = new Intent(context, ProjectilerBroadcastReceiver.class);
        startIntent.setAction(ProjectilerBroadcastReceiver.ACTION_START);
        PendingIntent startPendingIntent = PendingIntent.getBroadcast(context, 0, startIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        return startPendingIntent;
    }

    public static PendingIntent createResetPendingIntent(final Context context){
        Intent resetIntent = new Intent(context, ProjectilerBroadcastReceiver.class);
        resetIntent.setAction(ProjectilerBroadcastReceiver.ACTION_RESET);
        return PendingIntent.getBroadcast(context, 0, resetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static PendingIntent createStopPendingIntent(final Context context){

        UserDataStore dataStore = UserDataStore.getInstance(context);

        Intent resetIntent = new Intent(context, ProjectilerBroadcastReceiver.class);
        resetIntent.setAction(ProjectilerBroadcastReceiver.ACTION_STOP);
        resetIntent.putExtra(ProjectilerBroadcastReceiver.EXTRAS_START_DATE, dataStore.getStartDate().getTime());
        return PendingIntent.getBroadcast(context, 0, resetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
