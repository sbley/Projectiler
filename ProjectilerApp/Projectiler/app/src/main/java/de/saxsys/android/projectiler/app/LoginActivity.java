package de.saxsys.android.projectiler.app;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import de.saxsys.android.projectiler.app.backend.Projectiler;
import de.saxsys.android.projectiler.app.backend.UserDataStore;
import de.saxsys.android.projectiler.app.crawler.CrawlingException;


public class LoginActivity extends ActionBarActivity {

    private Button login;
    private EditText username;
    private EditText password;
    private CheckBox saveLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_login);


        // click listener
        login = (Button) findViewById(R.id.btn_login);

        username = (EditText) findViewById(R.id.et_name);
        password = (EditText) findViewById(R.id.et_password);

        saveLogin = (CheckBox) findViewById(R.id.cb_save_login);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setProgressBarIndeterminateVisibility(true);
                new LoginTask(username.getText().toString(),password.getText().toString(), saveLogin.isChecked()).execute();
            }
        });


        if(UserDataStore.getInstance().getAutoLogin(getApplicationContext())){

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);

        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    private class LoginTask extends AsyncTask<Void, Void, String> {

        private final String username;
        private final String password;
        private final boolean saveLogin;

        LoginTask(final String username, final String password, final boolean saveLogin){
            this.username = username;
            this.password = password;
            this.saveLogin = saveLogin;
        }


        @Override
        protected String doInBackground(Void... voids) {

            Projectiler defaultProjectiler = Projectiler.createDefaultProjectiler();

            try {
                defaultProjectiler.saveCredentials(getApplicationContext(), username, password, saveLogin);
            } catch (CrawlingException e) {
                e.printStackTrace();
                return e.getMessage();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);

            setProgressBarIndeterminateVisibility(false);

            if(aVoid == null){
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }else{
                Crouton.makeText(LoginActivity.this, aVoid, Style.ALERT).show();
            }
        }
    }

}
