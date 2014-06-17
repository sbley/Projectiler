package de.saxsys.android.projectiler.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

import de.saxsys.android.projectiler.app.R;
import de.saxsys.android.projectiler.app.utils.BusinessProcess;
import de.saxsys.android.projectiler.app.utils.NotificationUtils;
import de.saxsys.android.projectiler.app.utils.SettingsUtils;

public class NotificationReceiver extends BroadcastReceiver {

    private final String TAG = NotificationReceiver.class.getSimpleName();


    private BusinessProcess businessProcess;

    public NotificationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        //Log.i("BootCompleteReceiver", "NotificationReceiver");

        boolean sendNotification = SettingsUtils.isSendNotification(context);
        boolean vibration = SettingsUtils.isVibration(context);
        String startWork = SettingsUtils.startWork(context);
        String endWork = SettingsUtils.endWork(context);
        String startBreak = SettingsUtils.startBreak(context);
        String endBreak = SettingsUtils.endBreak(context);

        // sind alle Werte gesetzt?
        if(!startBreak.equals("") && !endBreak.equals("") && !startWork.equals("") && !endWork.equals("")){

            //Log.i(TAG, "alle Daten gesetzt");

            if(sendNotification){
                //Log.i(TAG, "send notification");
                Date currentTime = new Date(System.currentTimeMillis());
                // samstag und sonntag fallen raus
                if(!isWeekend(currentTime)){

                    Date startWorkDate = getDate(startWork);
                    Date endWorkDate = getDate(endWork);
                    Date startBreakDate = getDate(startBreak);
                    Date endBreakDate = getDate(endBreak);

                    businessProcess = BusinessProcess.getInstance(context);

                    boolean isProjectStarted = isProjectStarted();

                    if(isProjectStarted){
                        //Log.i(TAG, "Projekt gestartet");
                        //Log.i(TAG, "startWork: " + getDate(startWork).toString());
                        // projekt ist gestartet und endWork ist vorbei
                        if(currentTime.after(endWorkDate)){
                            sendNotification(context, 101, context.getString(R.string.work_is_over_title), context.getString(R.string.work_is_over_message), vibration);
                        }

                        // Projekt gestartet und startBreak ist vorbei
                        if(currentTime.after(startBreakDate) && currentTime.before(endBreakDate)){
                            sendNotification(context, 102, context.getString(R.string.time_for_break_title), context.getString(R.string.time_for_break_message), vibration);
                        }

                    }else{
                        //Log.i(TAG, "kein Projekt gestartet");
                        // projekt ist nicht gestartet und es ist arbeitszeit
                        if((currentTime.after(startWorkDate) && currentTime.before(startBreakDate) || (currentTime.after(endBreakDate) && currentTime.before(endWorkDate)))){
                            sendNotification(context, 103, context.getString(R.string.no_track_started_title), context.getString(R.string.no_track_started_message), vibration);
                        }
                    }
                }
            }
        }else{
            Log.i(TAG, "es sind nicht alle Daten gesetzt!");
        }
    }

    private Date getDate(String time) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        String[] timeSplit = time.split(":");

        calendar.set(Calendar.HOUR, Integer.parseInt(timeSplit[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeSplit[1]));
        calendar.set(Calendar.AM_PM, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    private boolean isWeekend(final Date date){

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int day = calendar.get(Calendar.DAY_OF_WEEK);

        if(day == Calendar.SATURDAY || day == Calendar.SUNDAY){
            return true;
        }

        return false;
    }

    private boolean isProjectStarted() {
        Date startDate = businessProcess.getStartDate();

        if(startDate == null){
            return false;
        }

        return true;
    }

    private void sendNotification(Context context, int mNotificationId, String title, String text, boolean vibrate) {

        NotificationUtils.sendNotification(context, mNotificationId, title, text);

        if(vibrate){
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(1000);

        }
    }

}
