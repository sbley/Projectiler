package de.saxsys.android.projectiler.app.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import de.saxsys.android.projectiler.app.R;

public abstract class LoginDialog extends Dialog implements View.OnClickListener {
	private EditText etPassword;
	private EditText etUsername;

	public LoginDialog(final Context context) {
		super(context);
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_login);

		findViewById(R.id.btn_login).setOnClickListener(this);

		etUsername = (EditText) findViewById(R.id.et_name);
		etPassword = (EditText) findViewById(R.id.et_password);
	}

	public void onClick(final View view) {
		if (view.getId() == R.id.btn_login) {
			onLogin(etUsername.getText().toString(), etPassword.getText().toString());
		}
	}

	protected abstract void onLogin(final String username, final String password);
}
