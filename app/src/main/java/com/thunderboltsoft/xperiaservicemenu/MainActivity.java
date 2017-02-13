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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button btnOpenServiceMenu, btnManuallyOpenServiceMenu;
    SharedPreferences prefs;
    Boolean bShowInfoDialog;
    Intent intentOpenServiceMenu;
    Boolean bServiceMenuExists;
    TextView txtError, txtDisclaimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        btnOpenServiceMenu = (Button) findViewById(R.id.btnOpenServiceMenu);
        btnManuallyOpenServiceMenu = (Button) findViewById(R.id.btnManuallyOpenServiceMenu);

        createListeners();

        TextView txtDeviceInfoName = (TextView) findViewById(R.id.txtDeviceInfoName);
        txtError = (TextView) findViewById(R.id.error);
        txtDisclaimer = (TextView) findViewById(R.id.disclaimer_text);
        String deviceInfoName = getDeviceName();

        if (deviceInfoName.equals("")) {
            txtDeviceInfoName.setText(R.string.device_info_name_error);
        } else {
            txtDeviceInfoName.setText(getDeviceName());
        }

        bServiceMenuExists = setupStartServiceMenu();

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        bShowInfoDialog = prefs.getBoolean("showDialog", true);

        if (bShowInfoDialog) {
            showInfoDialog();
        }
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return " " + capitalize(model);
        } else {
            return " " + capitalize(manufacturer) + " " + model;
        }
    }


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
            case R.id.action_info:
                showInfoDialog();
                return true;
            case R.id.action_help:
                Intent intent = new Intent();
                intent.setClassName(this, "com.thunderboltsoft.xperiaservicemenu.AboutActivity"); // instead of HelpActivity, don't need it anymore
                startActivity(intent);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Opens the "How to use" dialog box
     */
    public void showInfoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Info");

        // Sets the view of the dialog box
        builder.setView(LayoutInflater.from(this).inflate(R.layout.dialog_info,
                null));

        // Ads a "OK" button to close the dialog box and return to where the
        // user was
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


        // If the user clicks on anywhere outside the dialog box, it will return
        // the user to where the user was
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                // OK, go back to Main menu
            }
        });

        builder.show();
    }

    private void createListeners() {
        btnOpenServiceMenu.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                startServiceMenu();
            }
        });

        btnManuallyOpenServiceMenu.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", "*#*#7378423#*#*", null)));
            }
        });
    }

    private boolean setupStartServiceMenu() {
        intentOpenServiceMenu = new Intent("android.intent.action.MAIN");
        intentOpenServiceMenu.setComponent(ComponentName.unflattenFromString("com.sonyericsson.android.servicemenu/com.sonyericsson.android.servicemenu.ServiceMainMenu"));

        intentOpenServiceMenu.addCategory("android.intent.category.LAUNCHER");

        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intentOpenServiceMenu, 0);
        boolean isIntentSafe = activities.size() > 0;

        if (isIntentSafe) {
            return true;
        } else {
            txtError.setText("ERROR: Unable to find the service menu apk on this device.\n\nPossible explanations are:\n1. You are running a custom ROM\n2. You are attempting to run this app on a non Sony device\n3. You may have changed the device ID to a non Sony device ID inside build.prop\n\nAlternatively you can try to manually open the service menu by dialing the special code. Click on the following button to be redirected to the phone dialer with the code pre-entered.");
            txtError.setTextColor(Color.RED);
            btnOpenServiceMenu.setVisibility(View.GONE);
            btnManuallyOpenServiceMenu.setVisibility(View.VISIBLE);
            txtDisclaimer.setVisibility(View.GONE);
            return false;
        }
    }

    private void startServiceMenu() {
        if (bServiceMenuExists) {
            startActivity(intentOpenServiceMenu);
        }
    }
}
