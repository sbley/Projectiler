package de.saxsys.android.projectiler.app;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import de.saxsys.android.projectiler.app.utils.BusinessProcess;
import de.saxsys.android.projectiler.app.utils.SettingsUtils;

public class NotificationService extends Service implements Runnable {

    private static final String TAG = NotificationService.class.getSimpleName();

    private Thread thread = null;
    private boolean running;

    public void setRunning(boolean running) {
        Log.i(TAG, "setRunning");
        this.running = running;
    }

    private void startThread() {
        Log.i(TAG, "startThread");
        thread = new Thread(this);
        thread.start();
    }

    private void stopThread() {
        Log.i(TAG, "stopThread");
        setRunning(false);
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind");
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        stopThread();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        int ret = super.onStartCommand(intent, flags, startId);


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.ic_projectctiler_notification)
                        .setContentTitle("initial")
                        .setContentText("initial");

        startForeground(R.string.foreground_service_started, mBuilder.build());

        thread = new Thread(this);
        startThread();
        return ret;
    }


    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    public void run() {
        Log.i(TAG, "run");

        CheckTimerTask task = new CheckTimerTask();


        Timer myTimer = new Timer();

        myTimer.schedule(task, 0, 1000 * 10);

    }

    private void sendNotification(int mNotificationId, String title, String text) {

        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.ic_projectctiler_notification)
                        .setContentTitle(title)
                        .setContentText(text);

        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

    class CheckTimerTask extends TimerTask {
        public void run() {

            boolean sendNotification = SettingsUtils.isSendNotification(getApplicationContext());
            boolean vibration = SettingsUtils.isVibration(getApplicationContext());
            String startWork = SettingsUtils.startWork(getApplicationContext());
            String endWork = SettingsUtils.endWork(getApplicationContext());
            String startBreak = SettingsUtils.startBreak(getApplicationContext());
            String endBreak = SettingsUtils.endBreak(getApplicationContext());

            BusinessProcess businessProcess = BusinessProcess.getInstance(getApplicationContext());


            Date startDate = businessProcess.getStartDate(getApplicationContext());

            // laeuft eine Zeit gerade?
            //if(startDate != null){

                if(sendNotification){

                    if(vibration){
                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        v.vibrate(500);

                    }

                    sendNotification(101, "Bla", "Blubb");

                }
            //}

        }
    }

}
