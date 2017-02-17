package com.infoforarun.arun.dgifinapp;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.lang.Object;

import static android.R.id.list;
import static com.infoforarun.arun.dgifinapp.R.string.settings;

public class CollectionReportActivity extends AppCompatActivity {

    ListView listview = null;
    TextView cntTv, sumTv;
    ImageView noDataIv;

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

        setContentView(R.layout.activity_collection_report);

        listview = (ListView)findViewById(R.id.collReportLv);
        cntTv	 = (TextView)findViewById(R.id.arTvCount);
        sumTv	 = (TextView)findViewById(R.id.arTvDueSum);
        noDataIv = (ImageView)findViewById(R.id.Lr_No_data_Iv);

        DBAdapter.init(this);
        showList();
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
        setContentView(R.layout.activity_collection_report);
    }

    public void showList() {
        ArrayList<CollectionModel> bdlist = new ArrayList<CollectionModel>();
        bdlist.clear();
        int paidCnt = 0, paidSum = 0;

        Cursor c1 = DBAdapter.getAllCollection();
        if(c1 != null && c1.moveToFirst() ) {
            if(c1.moveToFirst()) {
                do{
                    CollectionModel bdlistitems = new CollectionModel();

                    bdlistitems.setAccountNo(c1.getString(c1.getColumnIndex(DBAdapter.COLLECTION_KEY_ACC_NO)));
                    bdlistitems.setCustomerName(c1.getString(c1.getColumnIndex(DBAdapter.COLLECTION_KEY_CUST_NAME)));
                    bdlistitems.setPaidAmt(c1.getInt(c1.getColumnIndex(DBAdapter.COLLECTION_KEY_PAID_AMT)));

                    bdlist.add(bdlistitems);

                    paidCnt++;
                    paidSum = paidSum + c1.getInt(c1.getColumnIndex(DBAdapter.COLLECTION_KEY_PAID_AMT));

                } while(c1.moveToNext());
            }
            listview.setVisibility(View.VISIBLE);
            noDataIv.setVisibility(View.GONE);
        } else {
            listview.setVisibility(View.GONE);
            noDataIv.setVisibility(View.VISIBLE);
        }
        c1.close();

        cntTv.setText(String.valueOf(paidCnt));

        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.UK);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();

        symbols.setGroupingSeparator(',');
        formatter.setDecimalFormatSymbols(symbols);

        String sumVal  = "₹ " + formatter.format(paidSum); //String.valueOf(paidSum);
        sumTv.setText(sumVal);

        CollectionReportAdapter dbListAdapter = new CollectionReportAdapter(CollectionReportActivity.this, bdlist);
        listview.setAdapter(dbListAdapter);

    }
}
