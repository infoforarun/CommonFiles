package com.infoforarun.arun.dgifinapp;


import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

import static com.infoforarun.arun.dgifinapp.R.string.settings;
import static com.infoforarun.arun.dgifinapp.R.xml.pref_notification;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    int secs = 0;

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

        // Load the XML preferences file
        addPreferencesFromResource(pref_notification);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // Let's do something when my counter preference value changes
        if (key.equals("notification_frequency")) {
            ListPreference lp = (ListPreference)findPreference("notification_frequency");

            CharSequence currText = lp.getEntry();
            String currValue = lp.getValue();

            secs = Integer.parseInt(currValue);

            cancelSchedules();
            startSchedule();
        }

        if (key.equals("notifications_new_message")) {
            SwitchPreference sp = (SwitchPreference)findPreference("notifications_new_message");

            Boolean bl = sp.isChecked();
            if(bl)
                startSchedule();
            else
                cancelSchedules();
        }

        if (key.equals("language_select")) {
            ListPreference lp = (ListPreference)findPreference("language_select");

            String lanValue = lp.getValue();
            if(lanValue.equals("en")) {
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "en").commit();
                setLangRecreate("en");
            } else if (lanValue.equals("ta")) {
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "ta").commit();
                setLangRecreate("ta");
            } else {
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "en").commit();
                setLangRecreate("en");
            }
        }
    }

    public void startSchedule() {
        if(secs==0)
            secs=5;
        AlarmManager alarms = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), NotificationService.class);
        final PendingIntent pIntent = PendingIntent.getService(this, 12547822, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarms.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(), 1000*secs, pIntent);
        //alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(), 1000*secs, pIntent);
        toast("Notification Service Started with "+secs/60 + " Minutes intervals");
    }

    public void cancelSchedules() {

        Intent intent = new Intent(getApplicationContext(),NotificationService.class);
        final PendingIntent pIntent = PendingIntent.getService(this, 12547822, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarms = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarms.cancel(pIntent);
        toast("Service Canceled...");
    }

    public void toast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void setLangRecreate(String langval) {

        locale = new Locale(langval);
        Locale.setDefault(locale);
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        recreate();
    }

}
