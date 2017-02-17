package com.infoforarun.arun.dgifinapp;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class AccountHistoryActivity extends AppCompatActivity {

    ListView listview = null;
    TextView sumTv;
    ImageView noDataIv;
    int paidSum = 0;

    SharedPreferences settings;
    Configuration config;
    String lang;
    Locale locale;

    String testOutput = "{\"DataArray\":[\n" +
            "{\"Date\":\"01-01-2017\", \"Amount\":\"100\"},\n" +
            "{\"Date\":\"02-01-2017\", \"Amount\":\"102\"},\n" +
            "{\"Date\":\"03-01-2017\", \"Amount\":\"104\"},\n" +
            "{\"Date\":\"04-01-2017\", \"Amount\":\"106\"},\n" +
            "{\"Date\":\"07-01-2017\", \"Amount\":\"107\"},\n" +
            "{\"Date\":\"08-01-2017\", \"Amount\":\"109\"},\n" +
            "{\"Date\":\"09-01-2017\", \"Amount\":\"121\"},\n" +
            "{\"Date\":\"11-01-2017\", \"Amount\":\"124\"},\n" +
            "{\"Date\":\"15-01-2017\", \"Amount\":\"125\"},\n" +
            "{\"Date\":\"16-01-2017\", \"Amount\":\"128\"},\n" +
            "{\"Date\":\"17-01-2017\", \"Amount\":\"130\"},\n" +
            "{\"Date\":\"18-01-2017\", \"Amount\":\"140\"},\n" +
            "{\"Date\":\"03-01-2017\", \"Amount\":\"104\"},\n" +
            "{\"Date\":\"04-01-2017\", \"Amount\":\"106\"},\n" +
            "{\"Date\":\"07-01-2017\", \"Amount\":\"107\"},\n" +
            "{\"Date\":\"08-01-2017\", \"Amount\":\"109\"},\n" +
            "{\"Date\":\"09-01-2017\", \"Amount\":\"121\"},\n" +
            "{\"Date\":\"11-01-2017\", \"Amount\":\"124\"},\n" +
            "{\"Date\":\"15-01-2017\", \"Amount\":\"125\"},\n" +
            "{\"Date\":\"16-01-2017\", \"Amount\":\"128\"},\n" +
            "{\"Date\":\"17-01-2017\", \"Amount\":\"130\"},\n" +
            "{\"Date\":\"18-01-2017\", \"Amount\":\"140\"},\n" +
            "{\"Date\":\"19-01-2017\", \"Amount\":\"150\"}\n" +
            "]}";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settings = PreferenceManager.getDefaultSharedPreferences(this);
        config = getBaseContext().getResources().getConfiguration();
            lang = settings.getString("LANG", "en");
            locale = new Locale(lang);
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        setContentView(R.layout.activity_account_history);


        listview = (ListView)findViewById(R.id.ahCollReportLv);
        sumTv	 = (TextView)findViewById(R.id.ahTvDueSum);
        noDataIv = (ImageView)findViewById(R.id.ah_No_data_Iv);

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
        setContentView(R.layout.activity_account_history);
    }

    public void showList() {
        ArrayList<AccountHistoryModel> chlist = new ArrayList<AccountHistoryModel>();
        chlist.clear();

        String  TAG_DATA_ARRAY  = "DataArray",
                TAG_DATE        = "Date",
                TAG_PAID_AMT    = "Amount";

        try {
            JSONObject ja = new JSONObject(testOutput);

            JSONArray da = ja.getJSONArray(TAG_DATA_ARRAY);
            int daSize = da.length();

            if(daSize>0) {
                for (int i=0; i<daSize; i++) {
                    JSONObject data = da.getJSONObject(i);

                    String mDate = data.getString(TAG_DATE);
                    int mAmtPaid = data.getInt(TAG_PAID_AMT);

                    AccountHistoryModel chm = new AccountHistoryModel();

                    chm.setDate(mDate);
                    chm.setPaidAmt(mAmtPaid);
                    chm.setSno(i+1);

                    chlist.add(chm);
                    paidSum = paidSum + mAmtPaid;
                }
                noDataIv.setVisibility(View.GONE);
                listview.setVisibility(View.VISIBLE);
            }else {
                noDataIv.setVisibility(View.VISIBLE);
                listview.setVisibility(View.GONE);
            }
        } catch (final JSONException e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "[Error: processAccountHistoryData] " + e.getMessage(),
                            Toast.LENGTH_LONG)
                            .show();
                }
            });
        }

        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.UK);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();

        symbols.setGroupingSeparator(',');
        formatter.setDecimalFormatSymbols(symbols);

        String sumVal  = "₹ " + formatter.format(paidSum); //String.valueOf(paidSum);
        sumTv.setText(sumVal);

        AccountHistoryAdapter dbAccHistAdapter = new AccountHistoryAdapter(AccountHistoryActivity.this, chlist);
        listview.setAdapter(dbAccHistAdapter);

    }


}
