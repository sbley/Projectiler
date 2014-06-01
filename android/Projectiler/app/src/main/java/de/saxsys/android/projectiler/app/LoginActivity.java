package de.saxsys.android.projectiler.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import org.droidparts.activity.support.v7.ActionBarActivity;
import org.droidparts.annotation.inject.InjectSystemService;
import org.droidparts.annotation.inject.InjectView;
import org.droidparts.concurrent.task.AsyncTaskResultListener;

import java.util.Calendar;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import de.saxsys.android.projectiler.app.asynctasks.LoginAsyncTask;
import de.saxsys.android.projectiler.app.receiver.NotificationReceiver;
import de.saxsys.android.projectiler.app.utils.BusinessProcess;
import de.saxsys.android.projectiler.app.utils.WidgetUtils;


public class LoginActivity extends ActionBarActivity implements View.OnClickListener{

    @InjectView(id = R.id.btn_login, click = true)
    private Button btnLogin;
    @InjectView(id = R.id.et_name)
    private EditText username;
    @InjectView(id = R.id.et_password)
    private EditText password;

    @InjectSystemService
    private AlarmManager alarmManager;

    private BusinessProcess businessProcess;

    @Override
    public void onPreInject() {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startNotificationService();

        businessProcess = BusinessProcess.getInstance(getApplicationContext());

        if(businessProcess.getAutoLogin()){

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);

            finish();
        }

    }

    private void startNotificationService() {
        Intent notification = new Intent(getApplicationContext(), NotificationReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, notification, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                1000 * 60 * 3, pendingIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public void onClick(View view) {
        if(view == btnLogin){
            setProgressBarIndeterminateVisibility(true);
            new LoginAsyncTask(getApplicationContext(), username.getText().toString(),password.getText().toString(), true, loginTaskListener).execute();
        }
    }

    private AsyncTaskResultListener<Void> loginTaskListener = new AsyncTaskResultListener<Void>() {
        @Override
        public void onAsyncTaskSuccess(Void aVoid) {
            setProgressBarIndeterminateVisibility(false);
            WidgetUtils.refreshWidget(getApplicationContext());

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);

            finish();
        }

        @Override
        public void onAsyncTaskFailure(Exception e) {
            setProgressBarIndeterminateVisibility(false);
            Crouton.makeText(LoginActivity.this, e.getMessage(), Style.ALERT).show();
        }
    };

}
