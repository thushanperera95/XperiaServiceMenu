package com.kaozgamer.easyaboutandfeedback;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 * About Activity for the About Activity fragment.
 *
 * @author Thushan Perera
 * @version 4
 */
public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Toolbar toolbar;

        setContentView(R.layout.fragment_empty_toolbar);

        // We want the toolbar so that the user can go back to the MainActivity
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Set the new view
        getFragmentManager().beginTransaction().replace(R.id.content_frame, new AboutFragment()).commit();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_help, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) { // If user clicks on back button, go to the previous activity
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_help_report_problem) { // DId user click on the report bug button on the toolbar?
            Intent mailto = new Intent(Intent.ACTION_SEND);
            mailto.setType("message/rfc822");
            mailto.putExtra(Intent.EXTRA_EMAIL, new String[]{"kaozgamerdev+grade_calculator@gmail.com"});
            mailto.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.help_activity_email_bug_report_subject));
            mailto.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.help_activity_email_bug_report_text));
            startActivity(Intent.createChooser(mailto, getResources().getString(R.string.email_intent_chooser)));

            return true;

        } else if (item.getItemId() == R.id.action_help_give_feedback) { // Did user click on the give feedback button on the toolbar?
            Intent mailto = new Intent(Intent.ACTION_SEND);
            mailto.setType("message/rfc822");
            mailto.putExtra(Intent.EXTRA_EMAIL, new String[]{"kaozgamerdev+grade_calculator@gmail.com"});
            mailto.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.help_activity_email_feedback_subject));
            mailto.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.help_activity_email_feedback_text));
            startActivity(Intent.createChooser(mailto, getResources().getString(R.string.email_intent_chooser)));

            return true;

        } else if (item.getItemId() == R.id.action_help_about) { // Did user click on the about button on the toolbar?
            Intent intent = new Intent();
            intent.setClassName(this, "com.thunderboltsoft.finalgradecalculator.activities.AboutActivity");
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
