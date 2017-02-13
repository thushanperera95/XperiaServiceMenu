package com.thunderboltsoft.xperiaservicemenu;


import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button b, exitB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        b = (Button) findViewById(R.id.button1);

        createListeners();

        showInfoDialog();
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

        b.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                startServiceMenu();
            }
        });
    }

    private void startServiceMenu() {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setComponent(ComponentName.unflattenFromString("com.sonyericsson.android.servicemenu/com.sonyericsson.android.servicemenu.ServiceMainMenu"));
        intent.addCategory("android.intent.category.LAUNCHER");

        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
        boolean isIntentSafe = activities.size() > 0;

        if (isIntentSafe) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "Unable to find the service menu app on this device", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

}
