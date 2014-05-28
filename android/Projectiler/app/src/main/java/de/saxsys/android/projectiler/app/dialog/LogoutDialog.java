package de.saxsys.android.projectiler.app.dialog;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

import de.saxsys.android.projectiler.app.LoginActivity;
import de.saxsys.android.projectiler.app.R;
import de.saxsys.android.projectiler.app.utils.BusinessProcess;

/**
 * Created by spaxx86 on 21.05.2014.
 */
public class LogoutDialog extends BaseDefaultDialogFragment {

    private BusinessProcess businessProcess;

    public LogoutDialog(){
    }

    @Override
    protected View getDialogView(LayoutInflater inflater) {
        businessProcess = BusinessProcess.getInstance(getActivity().getApplicationContext());
        return inflater.inflate(R.layout.dialog_logout, parentContainer, false);
    }

    @Override
    protected int getTitleId() {
        return R.string.action_logout;
    }

    @Override
    protected boolean hasNegativeButton() {
        return true;
    }

    @Override
    protected boolean hasPositiveButton() {
        return true;
    }

    @Override
    protected void onClickPositiveButton() {
        super.onClickPositiveButton();
        businessProcess.logout(getActivity().getApplication());

        Intent loginIntent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
        startActivity(loginIntent);
        getActivity().finish();

    }
}
