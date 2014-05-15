package de.saxsys.android.projectiler.app;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.List;

import de.saxsys.android.projectiler.app.backend.Projectiler;
import de.saxsys.android.projectiler.app.backend.UserDataStore;
import de.saxsys.android.projectiler.app.crawler.CrawlingException;
import de.saxsys.android.projectiler.app.ui.NavigationDrawerAdapter;
import de.saxsys.android.projectiler.app.utils.WidgetUtils;


public class NfcActivity extends ActionBarActivity {

    private ProgressBar progressBar;
    private ListView lvPorjects;
    private Projectiler projectiler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        lvPorjects = (ListView) findViewById(R.id.lvProjects);


        projectiler = Projectiler.createDefaultProjectiler();

        if (!projectiler.getAutoLogin(getApplicationContext())) {
            // Anzeige des Login
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);

        } else if (projectiler.getStartDate(getApplicationContext()) == null) {
            // Projekteauswahl anzeigen
            progressBar.setVisibility(View.VISIBLE);
            new GetProjectsAsyncTask().execute();

        } else {
            progressBar.setVisibility(View.VISIBLE);
            new CheckoutTask().execute();

        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nfc, menu);
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


    private class CheckoutTask extends AsyncTask {

        private String projectName;

        @Override
        protected Object doInBackground(Object[] objects) {
            projectName = projectiler.getProjectName(getApplicationContext());

            try {
                projectiler.checkout(getApplicationContext(), projectiler.getProjectName(getApplicationContext()));

                return new Object();

            } catch (CrawlingException e) {
                e.printStackTrace();
            } catch (IllegalStateException e1) {
                e1.printStackTrace();
                sendNotification(003, "Fehler beim Buchen der Zeit", e1.getMessage());

                finish();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            if (o != null) {
                progressBar.setVisibility(View.GONE);

                sendNotification(002, "Zeiterfassung beendet", "Projekt " + projectName + " gebucht!");

                WidgetUtils.refreshWidget(getApplicationContext());

                NfcActivity.this.finish();
            }

        }
    }

    private class GetProjectsAsyncTask extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Void... voids) {

            Projectiler defaultProjectiler = Projectiler.createDefaultProjectiler();
            try {

                List<String> projectNames = defaultProjectiler.getProjectNames(getApplicationContext());
                for (String projectName : projectNames) {
                    Log.i("Projects: ", "" + projectName);
                }

                return projectNames;
            } catch (CrawlingException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<String> itemList) {
            super.onPostExecute(itemList);

            int currentActiveIndex = UserDataStore.getInstance().getCurrentActiveIndex(getApplicationContext(), itemList);

            lvPorjects.setAdapter(new NavigationDrawerAdapter(getApplicationContext(), itemList, currentActiveIndex));

            progressBar.setVisibility(View.GONE);

            lvPorjects.setVisibility(View.VISIBLE);

            lvPorjects.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {

                    String projectName = (String) lvPorjects.getItemAtPosition(index);

                    projectiler.saveProjectName(getApplicationContext(), projectName);

                    new StartAsyncTask().execute();
                }
            });

        }
    }

    private class StartAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            projectiler.checkin(getApplicationContext());

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            progressBar.setVisibility(View.GONE);

            String startTime = projectiler.getStartDateAsString(getApplicationContext());

            String projectName = projectiler.getProjectName(getApplicationContext());

            sendNotification(001, "Zeiterfassung gestartet", startTime + " für das Projekt " + projectName);

            WidgetUtils.refreshWidget(getApplicationContext());


            finish();

        }
    }


    private void sendNotification(int mNotificationId, String title, String text) {

        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.ic_projectctiler_notification)
                        .setContentTitle(title)
                        .setContentText(text);

        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

}
