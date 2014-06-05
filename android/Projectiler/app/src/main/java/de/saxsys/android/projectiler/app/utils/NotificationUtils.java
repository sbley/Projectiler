package de.saxsys.android.projectiler.app.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import de.saxsys.android.projectiler.app.R;

/**
 * Created by stefan.heinze on 05.06.2014.
 */
public class NotificationUtils {

    public static void sendNotification(final Context context, int mNotificationId, String title, String text) {

        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_projectctiler_notification)
                        .setContentTitle(title)
                        .setContentText(text);

        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

}
