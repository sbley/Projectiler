package de.saxsys.android.projectiler.app;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import de.saxsys.android.projectiler.app.utils.BusinessProcess;


public class MainActivity extends org.droidparts.activity.support.v7.ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    public static final String NFC_KEY_WORD = "de.saxsys.android.projectile.app";
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private Tag projectilerTag;
    private NfcAdapter nfcAdapter;
    private PendingIntent nfcPendingIntent;
    private IntentFilter[] writeTagFilters;
    private Menu menu;
    private BusinessProcess businessProcess;


    @Override
    public void onPreInject() {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        businessProcess = BusinessProcess.getInstance();

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();


        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        setProgressBarIndeterminateVisibility(true);
        new GetProjectsAsyncTask(getApplicationContext()).execute();


        // gibt es bereits ein laufendes Projekt?
        if (businessProcess.getStartDate(getApplicationContext()) != null) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            TimeTrackingFragment fragment = new TimeTrackingFragment();

            fragment.setArguments(TimeTrackingFragment.newInstance(businessProcess.getProjectName(getApplicationContext()), false, true));

            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();


        }

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[]{tagDetected};


    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        String projectName = "";

        if (mNavigationDrawerFragment != null) {
            projectName = mNavigationDrawerFragment.getProjectName(position);
            // update the main content by replacing fragments
            FragmentManager fragmentManager = getSupportFragmentManager();

            TimeTrackingFragment fragment = new TimeTrackingFragment();

            String currentProjectName = businessProcess.getProjectName(getApplicationContext());

            Log.i("Projekt", "gestartetes Projekt: " + currentProjectName + " selectes Project: " + projectName);

            // beide Buttons sichtbar, weil kein aktives projekt
            if (currentProjectName.equals("")) {
                fragment.setArguments(TimeTrackingFragment.newInstance(projectName, true, false));

            } else if (currentProjectName.equals(projectName) && businessProcess.getStartDate(getApplicationContext()) != null) {
                fragment.setArguments(TimeTrackingFragment.newInstance(projectName, false, true));
            } else {
                fragment.setArguments(TimeTrackingFragment.newInstance(projectName, true, false));
            }


            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();

        }
    }

    public void onSectionAttached(String number) {

        mTitle = number;

    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);

        if (mTitle.equals("")) {
            mTitle = getString(R.string.app_name);
        }

        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        this.menu = menu;

        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.

            if (businessProcess.getAutoLogin(getApplicationContext())) {
                getMenuInflater().inflate(R.menu.main, menu);
            }
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_logout) {

            businessProcess.logout(getApplicationContext());

            finish();
        } else if (id == R.id.action_nfc) {
            try {

                if (projectilerTag != null) {

                    // nfc löschen
                    Log.d("", "write nfc");

                    write(NFC_KEY_WORD, projectilerTag);
                    Crouton.makeText(MainActivity.this, getString(R.string.ncf_in_range), Style.CONFIRM).show();
                } else {
                    // bitte NFC hinlegen
                    Crouton.makeText(MainActivity.this, "Bitte legen Sie den NFC Tag in Reichweite", Style.CONFIRM).show();
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (FormatException e) {
                e.printStackTrace();
            }
        }
        return super.onOptionsItemSelected(item);
    }


    public void refreshNavigationDrawer(String projectName) {

        if (businessProcess.getStartDate(getApplicationContext()) != null) {
            mNavigationDrawerFragment.setProjectActive(projectName);
        } else {
            mNavigationDrawerFragment.setProjectActive("");
        }
    }


    private class GetProjectsAsyncTask extends org.droidparts.concurrent.task.AsyncTask<Void, Void, List<String>> {


        public GetProjectsAsyncTask(Context ctx) {
            super(ctx);
        }

        @Override
        protected List<String> onExecute(Void... voids) throws Exception {
            return businessProcess.getProjectNames(getApplicationContext());
        }

        @Override
        protected void onPostExecuteFailure(Exception exception) {
            super.onPostExecuteFailure(exception);
            setProgressBarIndeterminateVisibility(false);
            Crouton.makeText(MainActivity.this, getString(R.string.no_connection_to_server), Style.ALERT).show();
        }

        @Override
        protected void onPostExecuteSuccess(List<String> itemList) {
            super.onPostExecuteSuccess(itemList);
            setProgressBarIndeterminateVisibility(false);
            mNavigationDrawerFragment.setItems(itemList);

            if (businessProcess.getProjectName(getApplicationContext()).equals("")) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                TimeTrackingFragment fragment = new TimeTrackingFragment();

                fragment.setArguments(TimeTrackingFragment.newInstance("", false, true));

                fragmentManager.beginTransaction()
                        .replace(R.id.container, fragment)
                        .commit();
            }
        }
    }


    public void onResume() {
        super.onResume();
        enableForegroundMode();
    }

    @Override
    protected void onPause() {
        super.onPause();
        disableForegroundMode();
    }


    // NFC Methods
    public void enableForegroundMode() {
        Log.d("", "enableForegroundMode");

        if (nfcAdapter != null) {
            IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED); // filter for tags
            IntentFilter[] writeTagFilters = new IntentFilter[]{tagDetected};
            nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, writeTagFilters, null);
        }

    }

    public void disableForegroundMode() {
        Log.d("", "disableForegroundMode");
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    private void write(String text, Tag tag) throws IOException, FormatException {

        NdefRecord[] records = {createRecord(text)};
        NdefMessage message = new NdefMessage(records);
        Ndef ndef = Ndef.get(tag);
        ndef.connect();
        ndef.writeNdefMessage(message);
        ndef.close();
    }

    private NdefRecord createRecord(String text) throws UnsupportedEncodingException {

        //create the message in according with the standard
        String lang = "en";
        byte[] textBytes = text.getBytes();
        byte[] langBytes = lang.getBytes("US-ASCII");
        int langLength = langBytes.length;
        int textLength = textBytes.length;

        byte[] payload = new byte[1 + langLength + textLength];
        payload[0] = (byte) langLength;

        // copy langbytes and textbytes into payload
        System.arraycopy(langBytes, 0, payload, 1, langLength);
        System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);

        NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload);
        return recordNFC;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("", "onNewIntent");

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            projectilerTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            Log.d("", "projectilerTag gesetzt");
            Crouton.makeText(MainActivity.this, getString(R.string.ncf_in_range), Style.CONFIRM).show();

            MenuItem item = menu.findItem(R.id.action_nfc);
            if (item == null) {
                getMenuInflater().inflate(R.menu.nfcitem, menu);
            }

        }

    }

}
