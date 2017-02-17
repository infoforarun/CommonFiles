package com.infoforarun.arun.dgifinapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v4.app.NotificationCompat;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.R.attr.id;
import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class NotificationService extends Service {

    String urlWf = "http://59.92.231.183:4343/DgiFin/help/operations/GetWorkFlowDetails";
    String errorStr;
    String mTagLineNumber, mTagCustomerId, exceptionMsg, result;

    String resp = "{\n" +
            "\t\"ResponseDesc\":\"Sucess\",\n" +
            "\t\"ResposeCode\":\"0\",\n" +
            "\t\"WorkflowDetails\":[{\n" +
            "\t\t\"ApprovedCustDate\":\"05-02-2017\",\n" +
            "\t\t\"ApprovedCustID\":\"C001\",\n" +
            "\t\t\"Category\":\"User Create\",\n" +
            "\t\t\"CustomerID\":\"ARUN001\",\n" +
            "\t\t\"Description\":\"Some Desc\",\n" +
            "\t\t\"LineNumber\":\"LN001\",\n" +
            "\t\t\"MainTask\":\"Some task\",\n" +
            "\t\t\"PrimaryKey\":\"PrimaryKey \",\n" +
            "\t\t\"Purpose\":\"Some Purpose Here\",\n" +
            "\t\t\"RequestStatus\":\"1\",\n" +
            "\t\t\"RequestedDate\":\"05-02-2017\",\n" +
            "\t\t\"SourceID\":\"SRC01\"\n" +
            "\t}]\n" +
            "}";

    Vibrator vibrator;

    public NotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new GetNotification().execute();
        stopSelf();

        return super.onStartCommand(intent, flags, startId);
    }

    private class GetNotification extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... arg0) {

                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                JSONObject js = new JSONObject();

                String TAG_CUSTOMERID = "CustomerID";

                String mCustomerId = DBAdapter.getCustomerId();

                try {
                    js.put(TAG_CUSTOMERID, mCustomerId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            String TAG_REPONSE_CD   = "ResposeCode",
                    TAG_REPONSE_DESC = "ResponseDesc",
                    TAG_WORKFLOW_DET = "WorkflowDetails";

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

            String mResponseDesc, mResponseCd;
            String rspnseMsg = resp;


            try {
                JSONObject response = new JSONObject(resp);

                mResponseCd = response.getString(TAG_REPONSE_CD);
                mResponseDesc = response.getString(TAG_REPONSE_DESC);

                if(mResponseCd.equals("0")) {
                    JSONArray dataArr = response.getJSONArray(TAG_WORKFLOW_DET);

                    int dataArrSize = dataArr.length();

                    if(dataArrSize>0) {
                        for(int i=0; i<dataArrSize; i++) {

                            JSONObject data = dataArr.getJSONObject(i);

                            mTagLineNumber = data.getString(TAG_LINE_NUMBER);
                            mTagCustomerId = data.getString(TAG_CUSTOMER_ID);
                        }
                    } else {
                        result = "No Notification found";
                    }
                } else
                    result = mResponseDesc;

            }catch (JSONException e) {
                exceptionMsg = "[isJsonResponseValid: JSON Invalid] Error: " + e.getMessage();
            }

            /*

                JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                        Request.Method.POST, urlWf, js,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                String rspnseMsg = response.toString();

                                //REMOVE THIS WHEN LIVE

                                rspnseMsg = resp;

                                if(rspnseMsg!=null && !rspnseMsg.equals("") && !rspnseMsg.isEmpty()) {
                                    //Toast.makeText(getApplicationContext(), "Response: " + rspnseMsg, Toast.LENGTH_LONG).show();
                                    //processJson(response);
                                    String TAG_REPONSE_CD   = "ResposeCode",
                                            TAG_REPONSE_DESC = "ResponseDesc",
                                            TAG_WORKFLOW_DET = "WorkflowDetails";

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

                                    String mResponseDesc, mResponseCd;

                                    try {
                                        mResponseCd = response.getString(TAG_REPONSE_CD);
                                        mResponseDesc = response.getString(TAG_REPONSE_DESC);

                                        if(mResponseCd.equals("0")) {
                                            JSONArray dataArr = response.getJSONArray(TAG_WORKFLOW_DET);

                                            int dataArrSize = dataArr.length();

                                            if(dataArrSize>0) {
                                                for(int i=0; i<dataArrSize; i++) {

                                                    JSONObject data = dataArr.getJSONObject(i);

                                                    mTagLineNumber = data.getString(TAG_LINE_NUMBER);
                                                    mTagCustomerId = data.getString(TAG_CUSTOMER_ID);
                                                }
                                            } else {
                                                result = "No Notification found";
                                            }
                                        } else
                                            result = mResponseDesc;

                                    }catch (JSONException e) {
                                        exceptionMsg = "[isJsonResponseValid: JSON Invalid] Error: " + e.getMessage();
                                    }
                                }
                                else
                                    result = "Notification response received as NULL";
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
                            result = errorStr;
                        }
                        else {
                            //Toast.makeText(getApplicationContext(), "Error: Response received as NULL", Toast.LENGTH_LONG).show();
                            result = "Error: Response received as NULL";
                        }
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
*/


            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            //toast(mTagLineNumber);
            showNotification();
            super.onPostExecute(result);

        }
    }

    private void showNotification() {
        int NOTIFICATION_ID = (int) System.currentTimeMillis();
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("DgiFin")
                .setAutoCancel(true)
                .setColor(getResources().getColor(R.color.colorAccent))
                .setContentText("You got a Line Request from " + mTagLineNumber)
                .setSmallIcon(R.mipmap.finance_icon);

        Intent mainIntent = new Intent(this, CollectionEntryActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                NOTIFICATION_ID,
                mainIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        //builder.setDeleteIntent(NotificationEventReceiver.getDeleteIntent(this));
        final NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, builder.build());

        //Audio notification
        Uri notification1 = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification1);
        mp.start();

        //Vibrate
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean vibrateTrue = pref.getBoolean("notifications_new_message_vibrate", true);
        if(vibrateTrue) {
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(500);
        }
    }

    public void toast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
