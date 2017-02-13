package com.thunderboltsoft.xperiaservicemenu;


import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
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
	    }
	    else {
	    	Toast.makeText(this,  "Unable to find the service menu app on this device", Toast.LENGTH_LONG).show();
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
