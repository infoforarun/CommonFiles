package com.infoforarun.arun.dgifinapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;

public class ManageRequestActivity extends AppCompatActivity {

    ListView list;
    ManageRequestAdapter adapter;
    public ManageRequestActivity ManageRequestView = null;
    public ArrayList<ManageRequestModel> ManageRequestArray = new ArrayList<ManageRequestModel>();
    ImageView noDataIv;

    String TAG_APPRVD_CUST_DATE 	= "ApprovedCustDate",
            TAG_APPRVD_CUST_ID		= "ApprovedCustID",
            TAG_CATEGORY 			= "Category",
            TAG_CUSTOMER_ID			= "CustomerID",
            TAG_DESCRIPTION 		= "Description",
            TAG_LINE_NUMBER  		= "LineNumber",
            TAG_MAIN_TASK			= "MainTask",
            TAG_PRIMARY_KEY			= "PrimaryKey",
            TAG_PURPOSE				= "Purpose",
            TAG_REQUEST_STATUS		= "RequestStatus",
            TAG_REQUEST_DATE		= "RequestedDate",
            TAG_SOURCE_ID 			= "SourceID";

    String TAG_REPONSE_CD           = "ResposeCode",
            TAG_REPONSE_DESC        = "ResponseDesc",
            TAG_WORKFLOW_DET        = "WorkflowDetails";

    SharedPreferences settings;
    Configuration config;
    String lang;
    Locale locale;

    String resp = "{\n" +
            "\"ResponseDesc\":\"Its an error simulation\",\n" +
            "\"ResposeCode\":\"0\",\n" +
            "\"WorkflowDetails\":[{\n" +
            "\"ApprovedCustDate\":\"05-02-2017\",\n" +
            "\"ApprovedCustID\":\"C001\",\n" +
            "\"Category\":\"User Create\",\n" +
            "\"CustomerID\":\"ARUN001\",\n" +
            "\"Description\":\"Some Desc\",\n" +
            "\"LineNumber\":\"LN001\",\n" +
            "\"MainTask\":\"Some task\",\n" +
            "\"PrimaryKey\":\"PrimaryKey \",\n" +
            "\"Purpose\":\"Some Purpose Here\",\n" +
            "\"RequestStatus\":\"1\",\n" +
            "\"RequestedDate\":\"05-02-2017\",\n" +
            "\"SourceID\":\"SRC01\"},\n" +
            "{\n" +
            "\"ApprovedCustDate\":\"05-02-2017\",\n" +
            "\"ApprovedCustID\":\"ARUN123\",\n" +
            "\"Category\":\"Line Approved\",\n" +
            "\"CustomerID\":\"ARUN003\",\n" +
            "\"Description\":\"Some Desc\",\n" +
            "\"LineNumber\":\"LN002\",\n" +
            "\"MainTask\":\"Some task\",\n" +
            "\"PrimaryKey\":\"PrimaryKey \",\n" +
            "\"Purpose\":\"Some Purpose Here\",\n" +
            "\"RequestStatus\":\"1\",\n" +
            "\"RequestedDate\":\"04-02-2017\",\n" +
            "\"SourceID\":\"SRC01\"}\n" +
            "]\n" +
            "};" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        settings = PreferenceManager.getDefaultSharedPreferences(this);
        config = getBaseContext().getResources().getConfiguration();
            lang = settings.getString("LANG", "en");
            locale = new Locale(lang);
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_request);

        ManageRequestView = this;

        Resources res = getResources();
        noDataIv = (ImageView)findViewById(R.id.Lr_No_data_Iv);
        list = (ListView)findViewById(R.id.MngeRqst_List);

        setListData();

        if(ManageRequestArray.size()>0) {
            list.setVisibility(View.VISIBLE);
            noDataIv.setVisibility(View.GONE);
            adapter=new ManageRequestAdapter( ManageRequestView, ManageRequestArray, res );
            list.setAdapter(adapter);
        } else {
            list.setVisibility(View.GONE);
            noDataIv.setVisibility(View.VISIBLE);
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
        setContentView(R.layout.activity_manage_request);
    }

    public void onItemClick(int mPosition) {
        ManageRequestModel tempValues = (ManageRequestModel) ManageRequestArray.get(mPosition);

        Intent intent = new Intent(this, ManageRequestDetailActivity.class);
        //intent.putExtra("ManageRequestObject", tempValues.toString());
        intent.putExtra("ManageRequestObject", (Serializable) tempValues);
        startActivity(intent);
        //this.finish();
    }

    /****** Function to set data in ArrayList *************/
    public void setListData()
    {
        String mRequestor, mRequestDate, mLineNo, mCategory, mApprovedCustId, mApprovedCustDate, mDescription, mMainTask, mPrimaryKey, mPurpose, mRequestStatus, mSourceId;
        String mResponseDesc, mResponseCd;
        String rspnseMsg = resp;

        try {

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JSONObject js = new JSONObject();

            String TAG_CUSTOMERID = "CustomerID";

            //String mCustomerId = DBAdapter.getCustomerId();
            String mCustomerId = "sdf";

            try {
                js.put(TAG_CUSTOMERID, mCustomerId);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONObject response = new JSONObject(resp);

            mResponseCd = response.getString(TAG_REPONSE_CD);
            mResponseDesc = response.getString(TAG_REPONSE_DESC);

            if(mResponseCd.equals("0")) {
                JSONArray dataArr = response.getJSONArray(TAG_WORKFLOW_DET);

                int dataArrSize = dataArr.length();

                if(dataArrSize>0) {
                    for (int i = 0; i < dataArr.length(); i++) {

                        ManageRequestModel assignvals = new ManageRequestModel();
                        JSONObject lineObj = dataArr.getJSONObject(i);

                        mRequestor = lineObj.getString(TAG_CUSTOMER_ID);
                        mRequestDate = lineObj.getString(TAG_REQUEST_DATE);
                        mLineNo = lineObj.getString(TAG_LINE_NUMBER);
                        mCategory = lineObj.getString(TAG_CATEGORY);
                        mApprovedCustId = lineObj.getString(TAG_APPRVD_CUST_ID);
                        mApprovedCustDate = lineObj.getString(TAG_APPRVD_CUST_DATE);
                        mDescription = lineObj.getString(TAG_DESCRIPTION);
                        mMainTask = lineObj.getString(TAG_MAIN_TASK);
                        mPrimaryKey  = lineObj.getString(TAG_PRIMARY_KEY);
                        mPurpose  = lineObj.getString(TAG_PURPOSE);
                        mRequestStatus = lineObj.getString(TAG_REQUEST_STATUS);
                        mSourceId = lineObj.getString(TAG_SOURCE_ID);


                        assignvals.setCustId(mRequestor);
                        assignvals.setRequestDate(mRequestDate);
                        assignvals.setLineNo(mLineNo);
                        assignvals.setCategory(mCategory);
                        assignvals.setApprovedCustId(mApprovedCustId);
                        assignvals.setApprovedCustDate(mApprovedCustDate);
                        assignvals.setDescription(mDescription);
                        assignvals.setMainTask(mMainTask);
                        assignvals.setPrimaryKey(mPrimaryKey);
                        assignvals.setPurpose(mPurpose);
                        assignvals.setRequestStatus(mRequestStatus);
                        assignvals.setSourceId(mSourceId);

                        ManageRequestArray.add(assignvals);
                    }
                }
            } else {
                snackMe("Error: "+ mResponseDesc);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void snackMe(String message) {
        Snackbar.make(list, message, Snackbar.LENGTH_LONG).show();
    }
}
