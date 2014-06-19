package de.saxsys.android.projectiler.app.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.preview.support.v4.app.NotificationManagerCompat;
import android.preview.support.wearable.notifications.WearableNotifications;
import android.support.v4.app.NotificationCompat;

import de.saxsys.android.projectiler.app.R;
import de.saxsys.android.projectiler.app.backend.UserDataStore;

/**
 * Created by stefan.heinze on 05.06.2014.
 */
public class NotificationUtils {

    public static final int NOTIFICATION_START_TRACKING_NFC = 001;
    public static final int NOTIFICATION_STOP_TRACKING = 002;
    public static final int NOTIFICATION_ERROR_STOP_TRACKING = 003;
    public static final int NOTIFICATION_START_TRACKING = 04;


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


    public static void sendStartTrackingNotification(final Context context) {
        UserDataStore dataStore = UserDataStore.getInstance(context);

        NotificationCompat.BigTextStyle bigStyle = new NotificationCompat.BigTextStyle();
        bigStyle.setBigContentTitle(context.getString(R.string.project_started))
        .bigText(dataStore.getProjectName());

        // Create builder for the main notification
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_projectctiler_notification)
                        .setLargeIcon(BitmapFactory.decodeResource(
                                context.getResources(), R.drawable.ic_launcher_saxsys))
                        .setStyle(bigStyle)
                        .setUsesChronometer(true)
                        .addAction(R.drawable.ic_projectctiler_notification, context.getString(R.string.stop), IntentUtils.createStopPendingIntent(context))
                        .addAction(R.drawable.ic_projectctiler_notification, context.getString(R.string.reset), IntentUtils.createResetPendingIntent(context));

        NotificationCompat.BigTextStyle secondPageStyle = new NotificationCompat.BigTextStyle();
        secondPageStyle.setBigContentTitle(context.getString(R.string.project))
                .bigText(dataStore.getProjectName());

        Notification secondPageNotification =
                new NotificationCompat.Builder(context)
                        .setStyle(secondPageStyle)
                        .build();

        Notification twoPageNotification =
                new WearableNotifications.Builder(notificationBuilder)
                        .addPage(secondPageNotification)
                        .build();

        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(context);

        // Build the notification and issues it with notification manager.
        notificationManager.notify(NOTIFICATION_START_TRACKING, twoPageNotification);

    }

    public static void removeStartTrackingNotification(final Context context) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) context.getSystemService(ns);
        nMgr.cancel(NOTIFICATION_START_TRACKING);
    }

}
