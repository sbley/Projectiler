package de.saxsys.android.projectiler.app.dialog;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

import org.droidparts.concurrent.task.AsyncTaskResultListener;

import java.util.Date;

import de.saxsys.android.projectiler.app.R;
import de.saxsys.android.projectiler.app.asynctasks.StopAsyncTask;
import de.saxsys.android.projectiler.app.backend.DateUtil;
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
    private EditText etComment;

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
        etComment = (EditText) view.findViewById(R.id.et_comment);

        tpStart.setIs24HourView(true);
        tpStop.setIs24HourView(true);

        businessProcess = BusinessProcess.getInstance(getActivity().getApplicationContext());

        Date startDate = businessProcess.getStartDate(getActivity().getApplicationContext());

        Date endDate = new Date(System.currentTimeMillis());

        DateUtil.setDatePicker(tpStart, startDate);
        DateUtil.setDatePicker(tpStop, endDate);

        return view;
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

        businessProcess.saveComment(getActivity().getApplicationContext(), etComment.getText().toString());

        new StopAsyncTask(getActivity().getApplicationContext(), projectName, DateUtil.getDate(tpStart), DateUtil.getDate(tpStop), stopTaskResultListener).execute();

        okClicked = true;

    }

    public interface OnBackPressListener{
        public void onBackPress();
    }

}
