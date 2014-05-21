package de.saxsys.android.projectiler.app.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;

/**
 * Refactor: make getDialogView not abstract and call getDialogViewId (abstract getDialogViewId():int) make another not
 * abstract method initView(final View contentView) which gets called by getDialogView => method can be overwritten and
 * add functionality
 *
 * @author stefan.barth
 */
public abstract class BaseDefaultDialogFragment extends DialogFragment implements OnClickListener, OnShowListener {
    private final Dialog.OnClickListener nullListener = new Dialog.OnClickListener() {
        @Override
        public void onClick(final DialogInterface dialog, final int which) {

        }
    };

    protected AlertDialog dialog;
    protected Button btnNegative;
    protected Button btnNeutral;
    protected Button btnPositive;
    protected FrameLayout parentContainer;

    /**
     * Just init variable 'contentView' via inflater and return variable 'contentView'
     *
     * @param inflater
     * @return
     */
    protected abstract View getDialogView(final LayoutInflater inflater);

    protected int getNegativeButtonText() {
        return android.R.string.cancel;
    }

    protected int getNeutralButtonText() {
        return android.R.string.cancel;
    }

    protected int getPositiveButtonText() {
        return android.R.string.ok;
    }

    protected abstract int getTitleId();

    protected abstract boolean hasNegativeButton();

    protected boolean hasNeutralButton() {
        return false;
    }

    protected abstract boolean hasPositiveButton();

    protected void loadArguments() {

    }

    protected void loadValues() {

    }

    @Override
    public void onClick(final View v) {
        if (v == btnNegative) {
            onClickNegativeButton();
        } else if (v == btnNeutral) {
            onClickNeutralButton();
        } else if (v == btnPositive) {
            onClickPositiveButton();
        }
    }

    protected void onClickNegativeButton() {
        dialog.dismiss();
    }

    protected void onClickNeutralButton() {
        dialog.dismiss();
    }

    protected void onClickPositiveButton() {
        dialog.dismiss();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadArguments();
        loadValues();
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        parentContainer = new FrameLayout(getActivity());

        final View dialogView = getDialogView(getActivity().getLayoutInflater());
        parentContainer.addView(dialogView);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (hasNegativeButton()) {
            builder.setNegativeButton(getNegativeButtonText(), nullListener);
        }
        if (hasNeutralButton()) {
            builder.setNeutralButton(getNeutralButtonText(), nullListener);
        }
        if (hasPositiveButton()) {
            builder.setPositiveButton(getPositiveButtonText(), nullListener);
        }
        builder.setCancelable(false);
        builder.setTitle(getTitleId());
        builder.setView(parentContainer);

        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnShowListener(this);

        return dialog;
    }

    @Override
    public void onShow(final DialogInterface d) {
        btnNegative = dialog.getButton(Dialog.BUTTON_NEGATIVE);
        btnNegative.setOnClickListener(this);

        btnNeutral = dialog.getButton(Dialog.BUTTON_NEUTRAL);
        btnNeutral.setOnClickListener(this);

        btnPositive = dialog.getButton(Dialog.BUTTON_POSITIVE);
        btnPositive.setOnClickListener(this);
    }
}

