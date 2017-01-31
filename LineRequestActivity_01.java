package javaproject;

public class LineRequestActivity extends AppCompatActivity {

    static int counter=0;
    ListView list;
    LineRequestAdapter adapter;
    public LineRequestActivity LineRequestView = null;
    public ArrayList<LineRequestModel> LineRequestArray = new ArrayList<LineRequestModel>();
    private ProgressDialog progressBar;
    Button downloadBtn, requestBtn;
    ImageView noDataIv;
    int clickedItemPosition;
    String  TAG_LINE_NAME   = "LineName",
            TAG_LINE_NO     = "LineNo",
            TAG_LINE_STATUS = "LineStatus",
			TAG_RESPONSE_DESC= "ResponseDesc",
			TAG_RESPONSE_CD = "ResposeCode",
			TAG_CUSTOMER_ID = "CustomerID",
            errorStr,
            lineRequestUrl 		= "http://59.92.231.183:4343/Dgifin/CollectionDataGenerationRequest",
            collectionDataUrl 	= "http://59.92.231.183:4343/Dgifin/CollectionFileGeneration" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                        initiateDownloadLineData(tempValues);
                        progressBar = new ProgressDialog(LineRequestActivity.this);
                        progressBar.setCancelable(false);
                        progressBar.setMessage("Loading...");
                        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressBar.show();
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
                        initiateLineRequest(tempValues);
                        progressBar = new ProgressDialog(LineRequestActivity.this);
                        progressBar.setCancelable(false);
                        progressBar.setMessage("Loading...");
                        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressBar.show();
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
        } else if (tempValues.getLineStatus().equals("2")) {            //Available to Request
            requestBtn.setText("Request "+tempValues.getLineName().toUpperCase());
            downloadBtn.setText("Download");
            downloadBtn.setEnabled(false);
            requestBtn.setEnabled(true);
        } else if (tempValues.getLineStatus().equals("3")) {            //Requested
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
			mResponseCd = reponse.getString(TAG_RESPONSE_CD);
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
	
	public void initiateDownloadLineData(LineRequestModel line) {
        String mLineNo, mCustomerId;

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JSONObject js = new JSONObject();

        mLineNo     = line.getLineNo();
        // IMPORTANT //
        // FETCH CUSTOMER ID FROM DB //
        mCustomerId = " ";
        
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
			mResponseCd = reponse.getString(TAG_RESPONSE_CD);
			if(mResponseCd=="0") {
				JSONArray dataArr = reponse.getJSONArray(TAG_EXPORT_CSV);
				
				int downloadSize = dataArr.length();

				if(downloadSize>0) {
				
					for(int i=0; i<dataArr.length(); i++) {
						progressBar.setMessage("Downloading Users of "+ i +"/"+downloadSize);
						
						JSONObject data = dataArr.getJSONObject(i);
						
						String mAccountNo	 = data.getString(TAG_ACCOUNT_NO); 
						String mAccountODate = data.getString(TAG_ACCOUNT_ODT);
						String mBalanceAmt	 = data.getString(TAG_BALANCE_AMT);
						String mCustomerName = data.getString(TAG_CUSTOMER_NM);
						String mDueAmt		 = data.getString(TAG_DUE_AMT);
						String mMobileNo	 = data.getString(TAG_MOBILE_NO);
						
						// IMPORTANT 
						// INSERT INTO SQLITE DB
					}
				} else {
					snackMe("No data available to Download");
				}
				
			} else if(mResponseCd=="1") {
				mResponseDesc = response.getString(TAG_RESPONSE_DESC);
				snackMe(mResponseDesc);
			} else if(mResponseCd=="100")
				Toast.makeText(getApplicationContext(), "Error while parsing Download data", Toast.LENGTH_LONG).show();
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

}
