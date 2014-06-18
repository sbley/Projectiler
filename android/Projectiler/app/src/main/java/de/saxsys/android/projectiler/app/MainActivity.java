package de.saxsys.android.projectiler.app;

import android.app.PendingIntent;
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

import com.github.amlcurran.showcaseview.ShowcaseView;

import org.droidparts.activity.support.v7.ActionBarActivity;
import org.droidparts.concurrent.task.AsyncTaskResultListener;
import org.droidparts.fragment.support.v4.Fragment;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import de.saxsys.android.projectiler.app.asynctasks.UploadAllTracksAsyncTask;
import de.saxsys.android.projectiler.app.db.DataProvider;
import de.saxsys.android.projectiler.app.dialog.LogoutDialog;
import de.saxsys.android.projectiler.app.ui.fragment.NavigationDrawerFragment;
import de.saxsys.android.projectiler.app.ui.fragment.TimeTrackingFragment;
import de.saxsys.android.projectiler.app.utils.BusinessProcess;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private static final String EXTRA_EVENT_ID = "event_id_start";
    private final String TAG = MainActivity.class.getSimpleName();

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
    private DataProvider dataProvider;
    private ShowcaseView.Builder helpDialog;
    private ShowcaseView showcaseView;

    @Override
    public void onPreInject() {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        businessProcess = BusinessProcess.getInstance(getApplicationContext());
        dataProvider = new DataProvider(getApplicationContext());

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();


        sendWearNotification();


        Log.d(TAG, "setup Navigation Drawer");
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        Log.d(TAG, "show TimeTrackingFragment");
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = TimeTrackingFragment.newInstance(businessProcess.getProjectName(), false, true);
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();

        Log.d(TAG, "setup NFC");
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[]{tagDetected};

    }

    private void sendWearNotification() {
/*
        int notificationId = 001;

// Create builder for the main notification
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_projectctiler_notification)
                        .setContentTitle("Page 1")
                        .setContentText("Short message");

// Create a big text style for the second page
        NotificationCompat.BigTextStyle secondPageStyle = new NotificationCompat.BigTextStyle();
        secondPageStyle.setBigContentTitle("Page 2")
                .bigText("A lot of text...");

// Create second page notification
        Notification secondPageNotification =
                new NotificationCompat.Builder(this)
                        .setStyle(secondPageStyle)
                        .build();

// Create main notification and add the second page
        Notification twoPageNotification =
                new WearableNotifications.Builder(notificationBuilder)
                        .addPage(secondPageNotification)
                        .build();

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);

        // Build the notification and issues it with notification manager.
        notificationManager.notify(notificationId, twoPageNotification);
*/





    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        String projectName = "";

        if (mNavigationDrawerFragment != null) {
            projectName = mNavigationDrawerFragment.getProjectName(position);

            // der navigation drawer ist noch nicht geladen
            if (projectName != null) {
                // update the main content by replacing fragments
                FragmentManager fragmentManager = getSupportFragmentManager();

                String currentProjectName = businessProcess.getProjectName();
                Date startDate = businessProcess.getStartDate();

                Log.d(TAG, "onNavigationDrawerItemSelected " + currentProjectName + " startDate: " + startDate);
                Log.d(TAG, "clicked on item " + projectName);

                boolean startVisible;
                boolean stopVisible;

                // beide Buttons sichtbar, weil kein aktives projekt
                if (currentProjectName.equals("")) {
                    startVisible = true;
                    stopVisible = false;
                } else if (currentProjectName.equals(projectName) && startDate != null) {
                    startVisible = false;
                    stopVisible = true;
                } else {
                    startVisible = true;
                    stopVisible = false;
                }

                Fragment fragment = TimeTrackingFragment.newInstance(projectName, startVisible, stopVisible);

                fragmentManager.beginTransaction()
                        .replace(R.id.container, fragment)
                        .commit();
            }
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

            if (dataProvider.getTracks().size() > 0) {
                getMenuInflater().inflate(R.menu.upload, menu);
            }

            if (businessProcess.getAutoLogin()) {
                getMenuInflater().inflate(R.menu.main, menu);
            }
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            case R.id.action_settings:
                Intent inten = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(inten);
                break;

            case R.id.action_logout:
                LogoutDialog dialogFragment = new LogoutDialog();
                dialogFragment.show(getFragmentManager(), "logoutDialog");
                break;
            case R.id.action_nfc:
                if (projectilerTag != null) {

                    // nfc löschen
                    Log.d(TAG, "write nfc");

                    try {
                        write(NFC_KEY_WORD, projectilerTag);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (FormatException e) {
                        e.printStackTrace();
                    }
                    Crouton.makeText(MainActivity.this, getString(R.string.ncf_in_range), Style.CONFIRM).show();
                } else {
                    // bitte NFC hinlegen
                    Crouton.makeText(MainActivity.this, getString(R.string.please_put_nfc_in_range), Style.CONFIRM).show();
                }
                break;
            case R.id.action_upload:
                setActionBarLoadingIndicatorVisible(true);
                new UploadAllTracksAsyncTask(getApplicationContext(), uploadAllTracksListener).execute();

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private AsyncTaskResultListener uploadAllTracksListener = new AsyncTaskResultListener() {
        @Override
        public void onAsyncTaskSuccess(Object o) {
            Log.i(TAG, "upload track successfull");
            setActionBarLoadingIndicatorVisible(false);
            Crouton.makeText(MainActivity.this, getString(R.string.tracks_was_uploaded), Style.CONFIRM).show();
            supportInvalidateOptionsMenu();
        }

        @Override
        public void onAsyncTaskFailure(Exception e) {
            Log.e(TAG, e.getMessage());
            setActionBarLoadingIndicatorVisible(false);
            Crouton.makeText(MainActivity.this, getString(R.string.no_connection_to_server), Style.ALERT).show();
            supportInvalidateOptionsMenu();
        }
    };


    public void refreshNavigationDrawer(String projectName) {
        Log.d(TAG, "refresh NavigationDrawer");
        if (businessProcess.getStartDate() != null) {
            mNavigationDrawerFragment.setProjectActive(projectName);
        } else {
            mNavigationDrawerFragment.setProjectActive("");
        }
    }


    // NFC Methods
    public void enableForegroundMode() {
        Log.d("", "enableForegroundMode");

        if (nfcAdapter != null) {
            IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED); // filter for tags
            IntentFilter[] writeTagFilters = new IntentFilter[]{tagDetected};
            nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, writeTagFilters, null);
        } else {
            Log.w(TAG, "NFC not possible");
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

    public void disableForegroundMode() {
        Log.d(TAG, "disableForegroundMode");
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
        Log.d(TAG, "onNewIntent");

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            projectilerTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            Log.d(TAG, "projectilerTag in range");
            Crouton.makeText(MainActivity.this, getString(R.string.ncf_in_range), Style.CONFIRM).show();

            MenuItem item = menu.findItem(R.id.action_nfc);
            if (item == null) {
                getMenuInflater().inflate(R.menu.nfcitem, menu);
            }
        }
    }
}
