package com.infoforarun.arun.dgifinapp;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class CollectionEntryActivity extends AppCompatActivity {

    EditText Collection_Amount_Et;
    TextView Mobile_Tv, Account_No_Tv, Name_Tv, Opening_date_Tv, Days_Spent_Tv, Opening_Balance_Tv, Due_Amount_Tv;
    Button Save_Btn, Next_Btn, Prev_Btn;

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

        setContentView(R.layout.activity_collection_entry);

        Collection_Amount_Et = (EditText)findViewById(R.id.Le_Collection_Amount_Et);
        Mobile_Tv = (TextView)findViewById(R.id.Le_Mobile_Tv);
        Account_No_Tv = (TextView)findViewById(R.id.Le_Account_No_Tv);
        Name_Tv = (TextView)findViewById(R.id.Le_Name_Tv);
        Opening_date_Tv = (TextView)findViewById(R.id.Le_Opening_date_Tv);
        Days_Spent_Tv = (TextView)findViewById(R.id.Le_Days_Spent_Tv) ;
        Opening_Balance_Tv = (TextView)findViewById(R.id.Le_Opening_Balance_Tv);
        Due_Amount_Tv = (TextView)findViewById(R.id.Le_Due_Amount_Tv);
        Save_Btn = (Button)findViewById(R.id.Le_Save_Btn);
        Next_Btn = (Button)findViewById(R.id.Le_Next_Btn);
        Prev_Btn = (Button)findViewById(R.id.Le_Previous_Btn);

        DBAdapter.init(this);

        CollectionModel collmodel = new CollectionModel();
        collmodel = DBAdapter.getNextPrevData("C");
        populateFields(collmodel);
        openKeyboard(false);
        Collection_Amount_Et.selectAll();
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
        setContentView(R.layout.activity_collection_entry);
    }

    public void populateFields(CollectionModel cm) {
        Collection_Amount_Et.setText(String.valueOf(cm.getDueAmt()));
        Name_Tv.setText(cm.getCustomerName());
        Mobile_Tv.setText(cm.getMobile());
        Account_No_Tv.setText(cm.getAccountNo());
        Opening_date_Tv.setText(cm.getAcntOpenDate());
        Days_Spent_Tv.setText(getDaysBetween(cm.getAcntOpenDate()));
        Opening_Balance_Tv.setText(String.valueOf(cm.getBalanceAmt()));
        Due_Amount_Tv.setText(String.valueOf(cm.getDueAmt()));

        if(cm.getSaved())
            Save_Btn.setEnabled(false);
        else
            Save_Btn.setEnabled(true);

        if(cm.getPrevNext().equals("N")) {
            Next_Btn.setEnabled(true);
            Prev_Btn.setEnabled(false);
        }
        else if (cm.getPrevNext().equals("P")) {
            Prev_Btn.setEnabled(true);
            Next_Btn.setEnabled(false);
        } else {
            Prev_Btn.setEnabled(true);
            Next_Btn.setEnabled(true);
        }
    }

    public void clearIbOnClick(View v) {
        if(Collection_Amount_Et.isEnabled()) {
            Collection_Amount_Et.setText("");
            Collection_Amount_Et.requestFocus();
            openKeyboard(true);
        }
    }

    public void onClickCallCustomer(View v) {
        String mobileNo = Mobile_Tv.getText().toString();
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:"+mobileNo));
        startActivity(intent);
    }

    public String getDaysBetween(String strStartDate) {
        int noOfDays = 0;
        SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");

        try {
            Date startDate = myFormat.parse(strStartDate);
            Date endDate = Calendar.getInstance().getTime();

            long diff = endDate.getTime() - startDate.getTime();
            noOfDays = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return String.valueOf(noOfDays);
    }

    public void setToast(String msg, int len) {
        if(len==0) {
            Toast.makeText(this.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this.getApplicationContext(), msg, Toast.LENGTH_LONG).show();
        }
    }

    public void prevBtnOnClick(View v) { prevbtnFunction(); }

    public void nextBtnOnClick(View v) { nextbtnFunction(); }

    public void saveBtnOnClick(View v) {

        if (!Collection_Amount_Et.getText().toString().isEmpty()) {
            AlertDialog.Builder confirmDialg = new AlertDialog.Builder(CollectionEntryActivity.this);

            confirmDialg.setTitle("Confirm");
            confirmDialg.setMessage(Html.fromHtml(
                    Account_No_Tv.getText().toString() +
                    "-" +
                    Name_Tv.getText().toString() +
                    "<br/>Paid " +
                    "<b> <font color=\"red\"> ₹ "+
                    Collection_Amount_Et.getText().toString() + "</font></b>" +
                    "<br/>Are you sure want to save?"));
            confirmDialg.setCancelable(false);
            confirmDialg.setIcon(R.mipmap.ic_action_warning);
            confirmDialg.setNegativeButton("Cancel", null);
            confirmDialg.setPositiveButton("Save", new AlertDialog.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {
                    String str = DBAdapter.saveCollection(Integer.parseInt(Account_No_Tv.getText().toString()), Integer.parseInt(Collection_Amount_Et.getText().toString()));

                    if (str.equals("Saved Successfully.")) {
                        nextbtnFunction();
                        openKeyboard(false);
                        Collection_Amount_Et.selectAll();
                    } else {
                        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
                    }
                }
            });
            confirmDialg.show();
        } else
            Toast.makeText(getApplicationContext(), "Enter Paid Due to save", Toast.LENGTH_SHORT).show();

    }



    public void nextbtnFunction(){
        CollectionModel cm = new CollectionModel();
        cm = DBAdapter.getNextPrevData("N");

        //Toast.makeText(getApplicationContext(), cm.getPrevNext(), Toast.LENGTH_SHORT).show();

        clearAllFields();
        populateFields(cm);
        openKeyboard(false);
        Collection_Amount_Et.selectAll();
    }

    public void prevbtnFunction() {
        CollectionModel cm = new CollectionModel();
        cm = DBAdapter.getNextPrevData("P");

        //Toast.makeText(getApplicationContext(), cm.getPrevNext(), Toast.LENGTH_SHORT).show();

        clearAllFields();
        populateFields(cm);
        openKeyboard(false);
        Collection_Amount_Et.selectAll();
    }

    public void clearAllFields(){
        Collection_Amount_Et.setText("");
        Mobile_Tv.setText("");
        Account_No_Tv.setText("");
        Name_Tv.setText("");
        Opening_date_Tv.setText("");
        Days_Spent_Tv.setText("");
        Opening_Balance_Tv.setText("");
        Due_Amount_Tv.setText("");
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(CollectionEntryActivity.this, DashboardActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_search:
                showInputDialog();
                openKeyboard(false);
                Collection_Amount_Et.selectAll();
                return true;
            case R.id.action_history:
                if(isOnline()) {
                    Intent i = new Intent(CollectionEntryActivity.this, AccountHistoryActivity.class);
                    startActivity(i);
                } else
                    checkOnline();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void showInputDialog() {

        LayoutInflater layoutInflater = LayoutInflater.from(CollectionEntryActivity.this);
        View promptView = layoutInflater.inflate(R.layout.collection_entry_search_input_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CollectionEntryActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);

        alertDialogBuilder.setIcon(R.mipmap.ic_search_black);
        alertDialogBuilder.setTitle("Search");
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(DBAdapter.isValidAccountNo(editText.getText().toString())) {
                            CollectionModel data1 = new CollectionModel();
                            data1 = DBAdapter.getCollectionForAccNo(editText.getText().toString());
                            clearAllFields();
                            populateFields(data1);
                            openKeyboard(false);
                            Collection_Amount_Et.selectAll();
                        } else {
                            Toast.makeText(getApplicationContext(), "Invalid Account No", Toast.LENGTH_SHORT).show();
                            openKeyboard(false);
                            Collection_Amount_Et.selectAll();
                        }
                        openKeyboard(false);
                        Collection_Amount_Et.selectAll();
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    public void openKeyboard(Boolean ok) {
        if(ok) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        } else {
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public Boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public void checkOnline() {
        if(!isOnline()) {
            Snackbar.make(Save_Btn, R.string.internet_offline, Snackbar.LENGTH_LONG)
                    .setAction(R.string.settings, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            startActivityForResult(new Intent(Settings.ACTION_SETTINGS), 0);
                        }
                    }).setActionTextColor(Color.RED)
                    .show();
        }
    }

    public void snackMe(String message) {
        Snackbar.make(Save_Btn, message, Snackbar.LENGTH_LONG).show();
    }

}
