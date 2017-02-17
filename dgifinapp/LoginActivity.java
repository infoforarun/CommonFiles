package com.infoforarun.arun.dgifinapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText mPasswordView, mUserIdView;
    private TextView mAppNameView;
    private View mProgressView;
    private View mLoginFormView;
    private String loginUrl= "http://59.92.231.183:4343/Dgifin/CheckLoginCredential";
    private ProgressDialog progressBar;
    private String errorStr;

    SharedPreferences settings;
    Configuration config;
    String lang;
    Locale locale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        DBAdapter.init(this);

        settings = PreferenceManager.getDefaultSharedPreferences(this);
        config = getBaseContext().getResources().getConfiguration();
            lang = settings.getString("LANG", "en");
            locale = new Locale(lang);
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUserIdView = (EditText) findViewById(R.id.la_userid_et);
        mLoginFormView = findViewById(R.id.login_form);
        mPasswordView = (EditText) findViewById(R.id.la_password_et);
        mAppNameView = (TextView) findViewById(R.id.lgn_welcome_tv);

        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/Eutemia.ttf");
        mAppNameView.setTypeface(type);

        //IMPORTANT: Remove this after app completed
        //
        mUserIdView.setText("ram123");
        mPasswordView.setText("1234");

        Button mEmailSignInButton = (Button) findViewById(R.id.la_signin_btn);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isUserNameValid() && !isPasswordValid()) {

                } else if (!isUserNameValid()) {

                } else if (!isPasswordValid()) {
                    test();
                } else if (!isOnline()) {
                    if (DBAdapter.offlineLoginEnabled()) {
                        initiateOfflineLogin();
                    } else
                        checkOnline();
                } else {
                    progressBar = new ProgressDialog(view.getContext());
                    progressBar.setCancelable(false);
                    progressBar.setMessage(getResources().getString(R.string.loading_str)+"...");
                    progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressBar.show();
                    //Enable when
                    initiateOnlineLogin();
                }
            }
        });
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
        setContentView(R.layout.activity_login);
    }

    public void test() {
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
    }

    public Boolean isUserNameValid() {
        if(mUserIdView.getText().toString().trim().isEmpty()) {
            mUserIdView.setError("Please enter your UserName");
            return false;
        }
        return true;
    }

    public Boolean isPasswordValid() {
        if(mPasswordView.getText().toString().trim().isEmpty()) {
            mPasswordView.setError("Please enter your Password");
            return false;
        }
        return true;
    }

    public void initiateOfflineLogin() {
        final String _un = DBAdapter.getUsername();
        String _pwd= DBAdapter.getPassword();

        if (_un.equals("NILL"))
            snackMe("Unable to Login Offline. Please go ONLINE.");
        else if (!_un.equals(mUserIdView.getText().toString())  &&
                !_pwd.equals(mPasswordView.getText().toString()))
            snackMe("Invalid Username & Password");
        else if (!_un.equals(mUserIdView.getText().toString()))
            snackMe("Invalid Username");
        else if (!_pwd.equals(mPasswordView.getText().toString()))
            snackMe("Invalid Password");
        else {
            AlertDialog.Builder confirmDialg = new AlertDialog.Builder(LoginActivity.this);

            confirmDialg.setTitle(R.string.la_proceed_offline_title);
            confirmDialg.setMessage(R.string.la_proceed_offline_msg);
            confirmDialg.setCancelable(false);
            confirmDialg.setIcon(R.mipmap.ic_action_warning);
            confirmDialg.setNegativeButton(R.string.Cancel_str, null);
            confirmDialg.setPositiveButton(R.string.ok_str, new AlertDialog.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {
                    Toast.makeText(getApplicationContext(), R.string.welcome_str + " " +_un, Toast.LENGTH_LONG).show();

                    // Check Collection Started status here and Proceed.
                    // IMPORTANT
                    openCollectionActivity();
                }
            });
            confirmDialg.show();
        }
    }

    public void openCollectionActivity() {
        Intent i = new Intent(LoginActivity.this, CollectionEntryActivity.class);
        startActivity(i);
        this.finish();
    }

    public void initiateOnlineLogin() {

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JSONObject js = new JSONObject();

        String TAG_USERNAME = "Username";
        String TAG_PASSWORD = "Password";

        String mUserId= mUserIdView.getText().toString();
        String mPswd=mPasswordView.getText().toString();

        try {
            js.put(TAG_USERNAME, mUserId);
            js.put(TAG_PASSWORD, mPswd);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST, loginUrl, js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String rspnseMsg = response.toString();
                        if(rspnseMsg!=null && !rspnseMsg.equals("") && !rspnseMsg.isEmpty()) {
                            //Toast.makeText(getApplicationContext(), "Response: " + rspnseMsg, Toast.LENGTH_LONG).show();
                            processJson(response);
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
                        errorStr = "Request Timeout. Please try again.";
                    } else if (error instanceof NoConnectionError) {
                        errorStr = "No Internet Connection";
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

        queue.add(jsonObjReq);

    }

    public Boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public void checkOnline() {
        if(!isOnline()) {
            Snackbar.make(mUserIdView, R.string.internet_offline, Snackbar.LENGTH_LONG)
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
        Snackbar.make(mUserIdView, message, Snackbar.LENGTH_LONG).show();
    }

    public void processJson(JSONObject jsonResponse) {
        String TAG_REPONSE_CD   = "ResposeCode",
                TAG_REPONSE_DESC = "ResponseDesc",
                TAG_CUSTOMER_ID = "CustomerID",
                TAG_LINE_NOS    = "Linenumbers",
                TAG_ROLE_ID     = "RoleID";

        final String exceptionMsg;

        String mResponseDesc, mCustomerID, mResponseCd, mRoleID = "5";
        try {
            mResponseCd = jsonResponse.getString(TAG_REPONSE_CD);
            mResponseDesc = jsonResponse.getString(TAG_REPONSE_DESC);
            mCustomerID = jsonResponse.getString(TAG_CUSTOMER_ID);
            mRoleID = jsonResponse.getString(TAG_ROLE_ID);

            if(Integer.parseInt(mResponseCd)==0) {
                String _un = mUserIdView.getText().toString();
                String _pw = mPasswordView.getText().toString();

                //Truncate and update the Metadata
                DBAdapter.trucateTable(DBAdapter.METADATA_TABLE);
                DBAdapter.insertMetadata(new MetadataModel(_un, _pw, "X", mCustomerID, false, true, "X"));

                JSONArray lineArr = jsonResponse.getJSONArray(TAG_LINE_NOS);

                if(DBAdapter.getCollectionStarted()) {
                    Intent intent = new Intent(this,DashboardActivity.class);
                    startActivity(intent);
                }
                else if (lineArr.length() > 0) {
                    Intent intent = new Intent(this,LineRequestActivity.class);
                    intent.putExtra("lineArray", lineArr.toString());
                    startActivity(intent);
                    this.finish();
                } else {
                    Intent intent = new Intent(this,DashboardActivity.class);
                    startActivity(intent);
                    this.finish();
                }
                progressBar.dismiss();
            } else {
                snackMe(mResponseDesc);
                progressBar.dismiss();
            }

        } catch (final JSONException e) {
            exceptionMsg = "[isJsonResponseValid: JSON Invalid] Error: " + e.getMessage();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), exceptionMsg, Toast.LENGTH_LONG).show();
                    progressBar.dismiss();
                }
            });
        }
    }

}