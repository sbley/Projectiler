package de.saxsys.android.projectiler.app;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import org.droidparts.activity.Activity;
import org.droidparts.annotation.inject.InjectView;

import java.util.Date;

import de.saxsys.android.projectiler.app.backend.DateUtil;
import de.saxsys.android.projectiler.app.service.ProjectilerBroadcastReceiver;
import de.saxsys.android.projectiler.app.utils.BusinessProcess;


public class CommentActivity extends Activity implements View.OnClickListener {

    @InjectView(id = R.id.btn_ok, click = true)
    private Button okButton;
    @InjectView(id = R.id.et_comment)
    private EditText etComment;
    @InjectView(id = R.id.tpStart)
    private TimePicker tpStart;
    @InjectView(id = R.id.tpStop)
    private TimePicker tpStop;

    private BusinessProcess businessProcess;

    @Override
    public void onPreInject() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_comment);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tpStart.setIs24HourView(true);
        tpStop.setIs24HourView(true);

        businessProcess = BusinessProcess.getInstance(getApplicationContext());

        Date startDate = businessProcess.getStartDate();

        Date endDate = new Date(System.currentTimeMillis());

        DateUtil.setDatePicker(tpStart, startDate);
        DateUtil.setDatePicker(tpStop, endDate);

    }

    @Override
    public void onClick(View view) {
        if(view == okButton){

            businessProcess.saveComment(etComment.getText().toString());

            Intent intent = new Intent(getApplicationContext(), ProjectilerBroadcastReceiver.class);
            intent.setAction(ProjectilerBroadcastReceiver.ACTION_STOP);
            intent.putExtra(ProjectilerBroadcastReceiver.EXTRAS_START_DATE, DateUtil.getDate(tpStart).getTime());
            intent.putExtra(ProjectilerBroadcastReceiver.EXTRAS_END_DATE, DateUtil.getDate(tpStop).getTime());
            PendingIntent stopPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);

            try {
                stopPendingIntent.send(getApplicationContext(), 0, intent);
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }

            finish();
        }
    }
}
