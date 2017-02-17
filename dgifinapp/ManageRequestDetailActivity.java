package com.infoforarun.arun.dgifinapp;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import static com.infoforarun.arun.dgifinapp.R.string.settings;

public class ManageRequestDetailActivity extends AppCompatActivity {

    TextView m_Approve_Cust_Id_Tv,
            m_Approve_Cust_Date_Tv,
            m_Category_Tv,
            m_Customer_ID_Tv,
            m_Description_Tv,
            m_Line_No_Tv,
            m_Main_Task_Tv,
            m_Primary_Key_Tv,
            m_Purpose_Tv,
            m_Request_Status_Tv,
            m_Request_Date_Tv,
            m_Source_ID_Tv;

    SharedPreferences settings;
    Configuration config;
    String lang;
    Locale locale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        settings = PreferenceManager.getDefaultSharedPreferences(this);
        config = getBaseContext().getResources().getConfiguration();
            lang = settings.getString("LANG", "en");
            locale = new Locale(lang);
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_request_detail);

        ManageRequestModel mRModel = (ManageRequestModel) getIntent().getSerializableExtra("ManageRequestObject");

        if(mRModel==null) throw new IllegalArgumentException();

        if(mRModel!=null) {

            m_Approve_Cust_Id_Tv    = (TextView)findViewById(R.id.Mrd_Approve_Cust_Id_Tv);
            m_Approve_Cust_Date_Tv  = (TextView)findViewById(R.id.Mrd_Approve_Cust_Date_Tv);
            m_Category_Tv           = (TextView)findViewById(R.id.Mrd_Category_Tv);
            m_Customer_ID_Tv        = (TextView)findViewById(R.id.Mrd_Customer_ID_Tv);
            m_Description_Tv        = (TextView)findViewById(R.id.Mrd_Description_Tv);
            m_Line_No_Tv            = (TextView)findViewById(R.id.Mrd_Line_No_Tv);
            m_Main_Task_Tv          = (TextView)findViewById(R.id.Mrd_Main_Task_Tv);
            m_Primary_Key_Tv        = (TextView)findViewById(R.id.Mrd_Primary_Key_Tv);
            m_Purpose_Tv            = (TextView)findViewById(R.id.Mrd_Purpose_Tv);
            m_Request_Status_Tv     = (TextView)findViewById(R.id.Mrd_Request_Status_Tv);
            m_Request_Date_Tv       = (TextView)findViewById(R.id.Mrd_Request_Date_Tv);
            m_Source_ID_Tv          = (TextView)findViewById(R.id.Mrd_Source_ID_Tv);

            m_Approve_Cust_Id_Tv.setText(mRModel.getApprovedCustId());
            m_Approve_Cust_Date_Tv.setText(mRModel.getApprovedCustDate());
            m_Category_Tv.setText(mRModel.getCategory());
            m_Customer_ID_Tv.setText(mRModel.getCustId());
            m_Description_Tv.setText(mRModel.getDescription());
            m_Line_No_Tv.setText(mRModel.getLineNo());
            m_Main_Task_Tv.setText(mRModel.getMainTask());
            m_Primary_Key_Tv.setText(mRModel.getPrimaryKey());
            m_Purpose_Tv.setText(mRModel.getPurpose());
            m_Request_Status_Tv.setText(mRModel.getRequestStatus());
            m_Request_Date_Tv.setText(mRModel.getRequestDate());
            m_Source_ID_Tv.setText(mRModel.getSourceId());

        }
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
}
