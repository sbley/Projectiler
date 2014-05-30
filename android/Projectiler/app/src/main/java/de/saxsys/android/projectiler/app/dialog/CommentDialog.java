package de.saxsys.android.projectiler.app.dialog;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import org.droidparts.concurrent.task.AsyncTaskResultListener;

import java.util.Calendar;
import java.util.Date;

import de.saxsys.android.projectiler.app.R;
import de.saxsys.android.projectiler.app.asynctasks.StopAsyncTask;
import de.saxsys.android.projectiler.app.utils.BusinessProcess;

/**
 * Created by stefan.heinze on 29.05.2014.
 */
@SuppressLint("ValidFragment")
public class CommentDialog extends BaseDefaultDialogFragment  {

    private TimePicker tpStart;
    private TimePicker tpStop;

    private AsyncTaskResultListener<Void> stopTaskResultListener;
    private OnBackPressListener backPressListener;
    private String projectName;
    private BusinessProcess businessProcess;
    private boolean okClicked = false;

    @SuppressLint("ValidFragment")
    public CommentDialog(final AsyncTaskResultListener<Void> stopTaskResultListener, OnBackPressListener backPressListener, final String projectName){
        this.stopTaskResultListener = stopTaskResultListener;
        this.projectName = projectName;
        this.backPressListener = backPressListener;
    }

    @Override
    protected View getDialogView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.dialog_comment, parentContainer, false);

        tpStart = (TimePicker) view.findViewById(R.id.tpStart);
        tpStop = (TimePicker) view.findViewById(R.id.tpStop);

        tpStart.setIs24HourView(true);
        tpStop.setIs24HourView(true);

        businessProcess = BusinessProcess.getInstance(getActivity().getApplicationContext());

        Date startDate = businessProcess.getStartDate(getActivity().getApplicationContext());

        Date endDate = new Date(System.currentTimeMillis());

        setDatePicker(tpStart, startDate);
        setDatePicker(tpStop, endDate);

        return view;
    }


    private void setDatePicker(final TimePicker timePicker, Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        timePicker.setCurrentHour(cal.get(Calendar.HOUR));
        timePicker.setCurrentMinute(cal.get(Calendar.MINUTE));

    }

    @Override
    protected int getTitleId() {
        return R.string.comment_title;
    }

    @Override
    protected boolean hasNegativeButton() {
        return false;
    }

    @Override
    protected boolean hasPositiveButton() {
        return true;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(!okClicked){
            backPressListener.onBackPress();
        }
    }

    @Override
    protected void onClickPositiveButton() {
        super.onClickPositiveButton();
        getActivity().setProgressBarIndeterminateVisibility(true);

        new StopAsyncTask(getActivity().getApplicationContext(), projectName, getDate(tpStart), getDate(tpStop), stopTaskResultListener).execute();

        okClicked = true;

    }

    private Date getDate(TimePicker timePicker) {

        Calendar cal = Calendar.getInstance();

        cal.setTime(new Date());
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        cal.set(Calendar.MINUTE, timePicker.getCurrentMinute());
        cal.set(Calendar.HOUR, timePicker.getCurrentHour());


        return cal.getTime();
    }

    public interface OnBackPressListener{
        public void onBackPress();
    }

}
