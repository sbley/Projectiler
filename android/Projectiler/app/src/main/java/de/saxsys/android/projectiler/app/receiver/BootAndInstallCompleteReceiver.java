package de.saxsys.android.projectiler.app.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.util.Calendar;

public class BootAndInstallCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("BootCompleteReceiver", "" + intent.getAction() + " " + context.getPackageName());

        if(intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED) ||intent.getAction().equals(Intent.ACTION_PACKAGE_FIRST_LAUNCH) ||intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED) || intent.getAction().equals(Intent.ACTION_PACKAGE_CHANGED) || intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            Uri data = intent.getData();
            String pkgName = data.getEncodedSchemeSpecificPart();
            Log.v("BootCompleteReceiver", "" + intent.getAction() + " " + pkgName);

            if(pkgName.equals(context.getPackageName())){

                Log.v("BootCompleteReceiver", "" + intent.getAction() + " " + context.getPackageName());

                Intent notification = new Intent(context, NotificationReceiver.class);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, notification, 0);

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());

                AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        1000 * 60, pendingIntent);

            }

        }

    }

}
