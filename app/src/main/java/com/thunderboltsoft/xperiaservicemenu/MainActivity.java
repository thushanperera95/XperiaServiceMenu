package com.thunderboltsoft.xperiaservicemenu;


import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.revmob.RevMob;
import com.revmob.RevMobAdsListener;
import com.revmob.ads.banner.RevMobBanner;

import java.util.List;

/**
 * The MainActivity of the app.
 * <p>
 * First checks the device for the Xperia service menu app and then allows user to open it. If not found, user is given option to manually open the service menu.
 *
 * @author Thushan Perera
 * @version 4
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Holds a reference to the open service menu button.
     */
    private Button btnOpenServiceMenu;

    /**
     * Holds a reference to the manually open service menu button.
     */
    private Button btnManuallyOpenServiceMenu;

    /**
     * Holds a reference to the shortcut to diagnostics button.
     */
    private Button btnShortcutDiagnostics;

    /**
     * Holds a reference to the SharedPreferences objects where info related to app is stored in between app launches.
     */
    private SharedPreferences prefs;

    /**
     * Holds reference to the intent that is used to open the service menu.
     */
    private Intent intOpenServiceMenu;

    /**
     * The boolean indicating if the service menu app exists on the device.
     */
    private Boolean bServiceMenuExists;

    /**
     * Holds a reference to the TextView that is responsible for displaying any error messages.
     */
    private TextView txtError;

    /**
     * Holds a reference to the TextView that is used to display the disclaimer.
     */
    private TextView txtDisclaimer;

    /**
     * Reference to the main Revmob object.
     */
    private RevMob revmob;

    /**
     * Reference to the Revmob ad banner.
     */
    private RevMobBanner banner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Get references to the views */
        btnOpenServiceMenu = (Button) findViewById(R.id.btnOpenServiceMenu);
        btnManuallyOpenServiceMenu = (Button) findViewById(R.id.btnManuallyOpenServiceMenu);
        btnShortcutDiagnostics = (Button) findViewById(R.id.btnShortcutDiagnostics);
        txtError = (TextView) findViewById(R.id.error);
        txtDisclaimer = (TextView) findViewById(R.id.disclaimer_text);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView txtDeviceInfoName = (TextView) findViewById(R.id.txtDeviceInfoName);

        /* Setup toolbar */
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        /* Create listeners for the buttons */
        createListeners();

        /* Get and display the display manufacturer and model number */
        String deviceInfoName = getDeviceName();
        if (deviceInfoName.equals("")) {
            txtDeviceInfoName.setText(R.string.device_info_name_error);
        } else {
            txtDeviceInfoName.setText(getDeviceName());
        }

        /* Perform search for the service menu apk */
        bServiceMenuExists = setupStartServiceMenu();

        /* Store the user preference for the how-to dialog box (show again or not) */
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean bShowInfoDialog = prefs.getBoolean("showDialog", true);
        if (bShowInfoDialog) {
            showInfoDialog();
        }

        /* Start the ad sessions */
        startRevMobSession();
    }

    /**
     * Gets the device manufacturer and model.
     *
     * @return formatted string containing manufacturer and model.
     */
    private String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;

        if (model.startsWith(manufacturer)) {
            return " " + capitalize(model);
        } else {
            return " " + capitalize(manufacturer) + " " + model;
        }
    }

    /**
     * Capitalize the first letter of a string.
     *
     * @param s string to capitalize
     * @return string with the first letter capitalized
     */
    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_info: // User clicks on how-to button on toolbar
                showInfoDialog();
                return true;
            case R.id.action_help: // User clicks on "About & Feedback" button on toolbar
                Intent intent = new Intent();
                intent.setClassName(this, "com.kaozgamer.easyaboutandfeedback.AboutActivity");
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Opens the "How to use" dialog box.
     */
    private void showInfoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Info");

        // Sets the view of the dialog box
        builder.setView(View.inflate(this, R.layout.dialog_info, null));

        // Ads a "OK" button to close the dialog box
        builder.setPositiveButton("OK",
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        // OK, go back to Main menu
                    }
                });

        builder.setNegativeButton("Don't Show Again",
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        // OK, never show this again
                        prefs.edit().putBoolean("showDialog", false).apply();
                    }
                });

        // If the user clicks on anywhere outside the dialog box, it will close the dialog box without doing anything
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                // OK, go back to Main menu
            }
        });
        builder.show();
    }

    /**
     * Creates the listeners for all the buttons i nthe view.
     */
    private void createListeners() {

        // Listener for the open service menu button
        btnOpenServiceMenu.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                startServiceMenu();
            }
        });

        // Listener for the open service menu manually button
        btnManuallyOpenServiceMenu.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", "*#*#7378423#*#*", null)));
            }
        });

        // Listener for the shortcut to diagnostics button
        btnShortcutDiagnostics.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.ACTION_DEVICE_INFO_SETTINGS));
            }
        });
    }

    /**
     * Finds the service menu app on the device otherwise displays an error message and allows user to manually open it.
     *
     * @return returns true if the service menu app was found, otherwise false.
     */
    private boolean setupStartServiceMenu() {
        // Intent to open the service menu app
        intOpenServiceMenu = new Intent("android.intent.action.MAIN");
        intOpenServiceMenu.setComponent(ComponentName.unflattenFromString("com.sonyericsson.android.servicemenu/com.sonyericsson.android.servicemenu.ServiceMainMenu"));
        intOpenServiceMenu.addCategory("android.intent.category.LAUNCHER");

        // Checks the list of apps on the device for the service menu
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intOpenServiceMenu, 0);
        boolean isIntentSafe = activities.size() > 0;

        if (isIntentSafe) { // Found the service menu app
            return true;
        } else { // Cannot find the service menu app
            txtError.setText("ERROR: Unable to find the service menu apk on this device.\n\nPossible explanations are:\n1. You are running a custom ROM\n2. You are attempting to run this app on a non Sony device\n3. You may have changed the device ID to a non Sony device ID inside build.prop\n\nAlternatively you can try to manually open the service menu by dialing the special code. Click on the following button to be redirected to the phone dialer with the code pre-entered.");
            txtError.setTextColor(Color.RED);

            // Hides button related to opening service menu, disclaimer and shortcut to diagnostics
            btnOpenServiceMenu.setVisibility(View.GONE);
            btnShortcutDiagnostics.setVisibility(View.GONE);
            txtDisclaimer.setVisibility(View.GONE);

            // Shows the button to manually open the service menu via code in the phone dialer
            btnManuallyOpenServiceMenu.setVisibility(View.VISIBLE);

            return false;
        }
    }

    /**
     * Opens the service menu app.
     */
    private void startServiceMenu() {
        if (bServiceMenuExists) {
            startActivity(intOpenServiceMenu);
        }
    }

    /**
     * Starts the Revmob session with the app ad ID.
     */
    private void startRevMobSession() {
        //RevMob's Start Session method:
        revmob = RevMob.startWithListener(this, new RevMobAdsListener() {
            @Override
            public void onRevMobSessionStarted() {
                loadBanner(); // Cache the banner once the session is started
                Log.i("RevMob", "Session Started");
            }

            @Override
            public void onRevMobSessionNotStarted(String message) {
                //If the session Fails to start, no ads can be displayed.
                Log.i("RevMob", "Session Failed to Start");
            }
        }, "58a172e4392e24746f5a849a");
    }

    /**
     * Downloads and loads the ad banner.
     */
    private void loadBanner() {
        banner = revmob.createBanner(this, new RevMobAdsListener() {
            @Override
            public void onRevMobAdReceived() {
                showBanner();
                Log.i("RevMob", "Banner Ready to be Displayed"); //At this point, the banner is ready to be displayed.
            }

            @Override
            public void onRevMobAdNotReceived(String message) {
                Log.i("RevMob", "Banner Not Failed to Load");
            }

            @Override
            public void onRevMobAdDisplayed() {
                Log.i("RevMob", "Banner Displayed");
            }
        });
    }

    /**
     * Displays the loaded ad banner onto the view.
     */
    private void showBanner() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ViewGroup view = (ViewGroup) findViewById(R.id.revmob_ad);
                view.addView(banner);
                banner.show(); //This method must be called in order to display the ad.
            }
        });
    }
}
