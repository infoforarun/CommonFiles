package com.infoforarun.arun.dgifinapp;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.view.View.OnClickListener;
import android.app.AlertDialog;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LineRequestActivity extends AppCompatActivity {

    static int counter=0;
    ListView list;
    LineRequestAdapter adapter;
    public LineRequestActivity LineRequestView = null;
    public ArrayList<LineRequestModel> LineRequestArray = new ArrayList<LineRequestModel>();
    private ProgressDialog progressBar;
    Button downloadBtn, requestBtn;
    ImageView noDataIv;
    int clickedItemPosition = 0;
    String  TAG_LINE_NAME   = "LineName",
            TAG_LINE_NO     = "LineNo",
            TAG_LINE_STATUS = "LineStatus",
            TAG_RESPONSE_DESC= "ResponseDesc",
            TAG_RESPONSE_CD = "ResposeCode",
            TAG_CUSTOMER_ID = "CustomerID",
            errorStr,
            lineRequestUrl 		= "http://59.92.231.183:4343/Dgifin/CollectionDataGenerationRequest",
            collectionDataUrl 	= "http://59.92.231.183:4343/Dgifin/CollectionFileGeneration" ;

    SharedPreferences settings;
    Configuration config;
    String lang;
    Locale locale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        DBAdapter.init(this);

        settings = PreferenceManager.getDefaultSharedPreferences(this);
        config = getBaseContext().getResources().getConfiguration();
            lang = settings.getString("LANG", "en");
            locale = new Locale(lang);
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        setContentView(R.layout.activity_line_request);

        setListData();

        LineRequestView = this;
        downloadBtn = (Button)findViewById(R.id.Lr_Download_Btn);
        requestBtn = (Button)findViewById(R.id.Lr_Request_Btn);
        noDataIv = (ImageView)findViewById(R.id.Lr_No_data_Iv);
        Resources res = getResources();
        list = (ListView)findViewById(R.id.Lr_List);

        requestBtn.setText("Request");
        requestBtn.setEnabled(false);
        downloadBtn.setText("Download");
        downloadBtn.setEnabled(false);

        if(LineRequestArray.size()>0) {
            list.setVisibility(View.VISIBLE);
            noDataIv.setVisibility(View.GONE);
            adapter = new LineRequestAdapter(LineRequestView, LineRequestArray, res);
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
        setContentView(R.layout.activity_line_request);
    }

    public void downloadBtnOnclick(View v) {

        if(isOnline()) {
            AlertDialog.Builder confirmDialg = new AlertDialog.Builder(LineRequestActivity.this);

            confirmDialg.setTitle("Confirm");
            confirmDialg.setMessage("Are you sure want to Download");
            confirmDialg.setCancelable(false);
            confirmDialg.setIcon(R.mipmap.ic_action_warning);
            confirmDialg.setNegativeButton("Cancel", null);
            confirmDialg.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {
                    if (isOnline()) {
                        LineRequestModel tempValues = (LineRequestModel) LineRequestArray.get(clickedItemPosition);
                        progressBar = new ProgressDialog(LineRequestActivity.this);
                        progressBar.setCancelable(false);
                        progressBar.setMessage("Loading...");
                        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressBar.show();
                        //initiateDownloadLineData(tempValues);
                        initiateDownloadLineData();
                    } else {
                        Snackbar.make(requestBtn, R.string.internet_offline, Snackbar.LENGTH_LONG)
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
            });
            confirmDialg.show();
        } else {
            Snackbar.make(requestBtn, R.string.internet_offline, Snackbar.LENGTH_LONG)
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

    public void requestBtnOnclick(View view) {
        if(isOnline()) {
            AlertDialog.Builder confirmDialg = new AlertDialog.Builder(LineRequestActivity.this);

            confirmDialg.setTitle("Confirm");
            confirmDialg.setMessage("Are you sure want to Request");
            confirmDialg.setCancelable(false);
            confirmDialg.setIcon(R.mipmap.ic_action_warning);
            confirmDialg.setNegativeButton("Cancel", null);
            confirmDialg.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {
                    if (isOnline()) {
                        LineRequestModel tempValues = (LineRequestModel) LineRequestArray.get(clickedItemPosition);
                        progressBar = new ProgressDialog(LineRequestActivity.this);
                        progressBar.setCancelable(false);
                        progressBar.setMessage("Loading...");
                        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressBar.show();
                        initiateLineRequest(tempValues);
                    } else {
                        Snackbar.make(requestBtn, R.string.internet_offline, Snackbar.LENGTH_LONG)
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
            });
            confirmDialg.show();
        } else {
            Snackbar.make(requestBtn, R.string.internet_offline, Snackbar.LENGTH_LONG)
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

    /****** Function to set data in ArrayList *************/
    public void setListData()
    {
        String mLineDesc, mLineNo, mLineStatus;
        try {
            String lineArray = getIntent().getStringExtra("lineArray");
            if(lineArray!=null && lineArray!="" ) {
                JSONArray lineArr = new JSONArray(getIntent().getStringExtra("lineArray"));

                for (int i = 0; i < lineArr.length(); i++) {

                    LineRequestModel assignvals = new LineRequestModel();
                    JSONObject lineObj = lineArr.getJSONObject(i);
                    mLineNo = lineObj.getString(TAG_LINE_NO);
                    mLineDesc = lineObj.getString(TAG_LINE_NAME);
                    mLineStatus = lineObj.getString(TAG_LINE_STATUS);

                    assignvals.setLineName(mLineDesc);
                    assignvals.setLineNo(mLineNo);
                    assignvals.setLineStatus(mLineStatus);

                    LineRequestArray.add(assignvals);
                }
            } else {
                //Toast.makeText(getApplicationContext(), "No line data found", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /*****************  This function used by adapter ****************/

    public void onItemClick(int mPosition)
    {
        LineRequestModel tempValues = (LineRequestModel) LineRequestArray.get(mPosition);

        clickedItemPosition = mPosition;

        if(tempValues.getLineStatus().equals("1")) {                   //Approved
            downloadBtn.setText("Download " + tempValues.getLineName().toUpperCase());
            requestBtn.setText("Request");
            requestBtn.setEnabled(false);
            downloadBtn.setEnabled(true);
        } else if (tempValues.getLineStatus().equals("3")) {            //Approved
            requestBtn.setText("Request "+tempValues.getLineName().toUpperCase());
            downloadBtn.setText("Download");
            downloadBtn.setEnabled(true);
            requestBtn.setEnabled(false);
        } else if (tempValues.getLineStatus().equals("2")) {            //Requested
            requestBtn.setText("Request");
            requestBtn.setEnabled(false);
            downloadBtn.setText("Download");
            downloadBtn.setEnabled(false);
        } else {
            requestBtn.setText("Request");
            requestBtn.setEnabled(false);
            downloadBtn.setText("Download");
            downloadBtn.setEnabled(false);
        }
    }

    public void initiateLineRequest(LineRequestModel line) {
        String mLineDesc, mLineNo, mLineStatus;

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JSONObject js = new JSONObject();

        mLineDesc  = line.getLineName();
        mLineNo    = line.getLineNo();
        mLineStatus= line.getLineStatus();

        try {
            js.put(TAG_LINE_NAME,   mLineDesc);
            js.put(TAG_LINE_NO,     mLineNo);
            js.put(TAG_LINE_STATUS, mLineStatus);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST, lineRequestUrl, js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String rspnseMsg = response.toString();
                        if(rspnseMsg!=null && !rspnseMsg.equals("") && !rspnseMsg.isEmpty()) {
                            Toast.makeText(getApplicationContext(), "Response: " + rspnseMsg, Toast.LENGTH_LONG).show();
                            processLineRequestJson(response);
                        }
                        else
                            Toast.makeText(getApplicationContext(), "Response: Response received as NULL" , Toast.LENGTH_LONG).show();
                        progressBar.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errMsg = error.toString();
                if(errMsg!=null && !errMsg.equals("") && !errMsg.isEmpty()) {
                    if (error instanceof TimeoutError) {
                        errorStr = "Timeout Error. Please try again or contact admin";
                    } else if (error instanceof NoConnectionError) {
                        errorStr = "No Internet Connection. Please check your Internet";
                    } else if (error instanceof AuthFailureError) {
                        errorStr = "Authentication Failure";
                    } else if (error instanceof ServerError) {
                        errorStr = "Server Error";
                    } else if (error instanceof NetworkError) {
                        errorStr = "Network Error";
                    } else if (error instanceof ParseError) {
                        errorStr = "Parse Error";
                    }
                    snackMe(errorStr);
                }
                else {
                    //Toast.makeText(getApplicationContext(), "Error: Response received as NULL", Toast.LENGTH_LONG).show();
                    snackMe("Error: Response received as NULL");
                }
                progressBar.dismiss();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        requestQueue.add(jsonObjReq);
    }

    //Process Line Request JSON Here
    public void processLineRequestJson(JSONObject response) {

        String mResponseCd, mResponseDesc;

        try {
            mResponseCd = response.getString(TAG_RESPONSE_CD);
            if(mResponseCd=="0") {
                snackMe("Line Request Completed. Once the request is approved, you can able to download");
            } else if(mResponseCd=="1") {
                mResponseDesc = response.getString(TAG_RESPONSE_DESC);
                Toast.makeText(getApplicationContext(), "Error: "+mResponseDesc, Toast.LENGTH_LONG).show();
            } else if(mResponseCd=="100")
                Toast.makeText(getApplicationContext(), "Error while parsing Line Request", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(getApplicationContext(), "Error: Invalid response.", Toast.LENGTH_LONG).show();
        } catch (final JSONException e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "[Error: processLineRequestJson] " + e.getMessage(),
                            Toast.LENGTH_LONG)
                            .show();
                }
            });
        }
    }

    //public void initiateDownloadLineData(LineRequestModel line) {
    public void initiateDownloadLineData() {
        String mLineNo, mCustomerId;

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JSONObject js = new JSONObject();

        LineRequestModel tempValues = (LineRequestModel) LineRequestArray.get(clickedItemPosition);

        mLineNo     = tempValues.getLineNo();
        mCustomerId = DBAdapter.getCustomerId();

        try {
            js.put(TAG_LINE_NO,     mLineNo);
            js.put(TAG_CUSTOMER_ID, mCustomerId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST, collectionDataUrl, js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String rspnseMsg = response.toString();
                        if(rspnseMsg!=null && !rspnseMsg.equals("") && !rspnseMsg.isEmpty()) {
                            Toast.makeText(getApplicationContext(), "Response: " + rspnseMsg, Toast.LENGTH_LONG).show();
                            processDownloadJson(response);
                        }
                        else
                            Toast.makeText(getApplicationContext(), "Response: Response received as NULL" , Toast.LENGTH_LONG).show();
                        progressBar.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errMsg = error.toString();
                if(errMsg!=null && !errMsg.equals("") && !errMsg.isEmpty()) {
                    if (error instanceof TimeoutError) {
                        errorStr = "Timeout Error. Please try again or contact admin";
                    } else if (error instanceof NoConnectionError) {
                        errorStr = "No Internet Connection. Please check your Internet";
                    } else if (error instanceof AuthFailureError) {
                        errorStr = "Authentication Failure";
                    } else if (error instanceof ServerError) {
                        errorStr = "Server Error";
                    } else if (error instanceof NetworkError) {
                        errorStr = "Network Error";
                    } else if (error instanceof ParseError) {
                        errorStr = "Parse Error";
                    }
                    snackMe(errorStr);
                }
                else {
                    //Toast.makeText(getApplicationContext(), "Error: Response received as NULL", Toast.LENGTH_LONG).show();
                    snackMe("Error: Response received as NULL");
                }
                progressBar.dismiss();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        requestQueue.add(jsonObjReq);

    }

    public void processDownloadJson(JSONObject response) {

        String mResponseCd, mResponseDesc;
        String 	TAG_EXPORT_CSV 	= "ExportCsv",
                TAG_ACCOUNT_NO	= "AccountNo",
                TAG_ACCOUNT_ODT	= "AccountOpenDate",
                TAG_BALANCE_AMT = "BalanceAmount",
                TAG_CUSTOMER_NM = "CustomerName",
                TAG_DUE_AMT		= "DueAmount",
                TAG_MOBILE_NO   = "MobileNo";

        try {
            mResponseCd = response.getString(TAG_RESPONSE_CD);

            if(mResponseCd.equals("0")) {
                JSONArray dataArr = response.getJSONArray(TAG_EXPORT_CSV);

                int downloadSize = dataArr.length();

                if(downloadSize>0) {

                    DBAdapter.trucateTable(DBAdapter.COLLECTION_TABLE);
                    DBAdapter.resetTableSequence(DBAdapter.COLLECTION_TABLE);

                    int dataArrLen = dataArr.length();

                    for(int i=0; i<dataArrLen; i++) {
                        progressBar.setMessage("Downloading Users of "+ Integer.parseInt(String.valueOf(i+1)) +"/"+downloadSize);

                        JSONObject data = dataArr.getJSONObject(i);

                        String mAccountNo	 = data.getString(TAG_ACCOUNT_NO);
                        String mAccountODate = data.getString(TAG_ACCOUNT_ODT);
                        String mBalanceAmt	 = data.getString(TAG_BALANCE_AMT);
                        int balanceAmt = Integer.parseInt(mBalanceAmt);
                        String mCustomerName = data.getString(TAG_CUSTOMER_NM);
                        String mDueAmt		 = data.getString(TAG_DUE_AMT);
                        int dueAmt = Integer.parseInt(mDueAmt);
                        String mMobileNo	 = data.getString(TAG_MOBILE_NO);

                        DBAdapter.insertCollection(new CollectionModel(balanceAmt, dueAmt, 0, mAccountNo,
                                                                    mAccountODate, mCustomerName, mMobileNo,
                                                                    false, false, "PN"));
                                                                    //DUMMY. Works only with PREV/NEXT Buttons

                    }
                    snackMe("Download Completed. "+ dataArrLen + " Users downloaded");
                } else {
                    snackMe("No data available to Download");
                }

            } else if(mResponseCd.equals("1")) {
                mResponseDesc = response.getString(TAG_RESPONSE_DESC);
                snackMe(mResponseDesc);
            } else if(mResponseCd.equals("100"))
                Toast.makeText(getApplicationContext(), "Error while parsing Download data", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(getApplicationContext(), "Error: Invalid response.", Toast.LENGTH_LONG).show();
            if(progressBar.isShowing())
                progressBar.dismiss();
        } catch (final JSONException e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "[Error: processLineRequestJson] " + e.getMessage(),
                            Toast.LENGTH_LONG)
                            .show();
                }
            });
        }
    }

    public Boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public void checkOnline() {
        if(!isOnline()) {
            Snackbar.make(requestBtn, R.string.internet_offline, Snackbar.LENGTH_LONG)
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
        Snackbar.make(requestBtn, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(LineRequestActivity.this, DashboardActivity.class);
        startActivity(intent);
    }

}