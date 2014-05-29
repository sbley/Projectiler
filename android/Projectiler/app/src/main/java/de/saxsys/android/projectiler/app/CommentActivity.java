package de.saxsys.android.projectiler.app;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import org.droidparts.activity.Activity;
import org.droidparts.annotation.inject.InjectView;

import de.saxsys.android.projectiler.app.service.ProjectilerIntentService;
import de.saxsys.android.projectiler.app.utils.BusinessProcess;


public class CommentActivity extends Activity implements View.OnClickListener {

    @InjectView(id = R.id.btn_ok, click = true)
    private Button okButton;
    @InjectView(id = R.id.et_comment)
    private EditText etComment;

    private BusinessProcess businessProcess;

    @Override
    public void onPreInject() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_comment);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        businessProcess = BusinessProcess.getInstance(getApplicationContext());

    }

    @Override
    public void onClick(View view) {
        if(view == okButton){

            businessProcess.saveComment(getApplicationContext(), etComment.getText().toString());

            Intent intent = new Intent(getApplicationContext(), ProjectilerIntentService.class);
            intent.setAction(ProjectilerIntentService.ACTION_STOP);
            PendingIntent stopPendingIntent = PendingIntent.getService(getApplicationContext(), 0, intent, 0);

            try {
                stopPendingIntent.send(getApplicationContext(), 0, intent);
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }

            finish();
        }
    }
}
