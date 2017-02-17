package com.infoforarun.arun.dgifinapp;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {

    SharedPreferences settings;
    Configuration config;
    String lang;
    Locale locale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settings = PreferenceManager.getDefaultSharedPreferences(this);
        config = getBaseContext().getResources().getConfiguration();
            lang = settings.getString("LANG", "en");
            locale = new Locale(lang);
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        setContentView(R.layout.activity_dashboard);

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit")
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DashboardActivity.this.finish();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        lang = settings.getString("LANG", Locale.getDefault().getLanguage());
        locale = new Locale(lang);
        config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());
        setContentView(R.layout.activity_dashboard);
    }

    public void linerequestOnclick(View view) {
        Intent i = new Intent(DashboardActivity.this, LineRequestActivity.class);
        startActivity(i);
    }

    public void collectionEntryOnclick(View view) {
        Intent i = new Intent(DashboardActivity.this, CollectionEntryActivity.class);
        startActivity(i);
    }

    public void appSettingsOnclick(View view) {
        Intent i = new Intent(DashboardActivity.this, SettingsActivity.class);
        startActivity(i);
    }

    public void appManageRequestOnclick(View view) {
        Intent i = new Intent(DashboardActivity.this, ManageRequestActivity.class);
        startActivity(i);
    }

    public void appCollectionReportOnclick(View view) {
        Intent i = new Intent(DashboardActivity.this, CollectionReportActivity.class);
        startActivity(i);
    }

    public void aboutOnclick(View view) {
        Toast.makeText(this, "Will be back soon :) ", Toast.LENGTH_SHORT).show();
    }
}
