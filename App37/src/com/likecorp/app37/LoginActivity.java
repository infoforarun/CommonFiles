package com.likecorp.app37;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.example.app37.R;

public class LoginActivity extends Activity {

	EditText usernameEt, pswdEt;
	private static final int PICKFILE_RESULT_CODE = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		DBAdapter.init(this);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);
		
		usernameEt = (EditText)findViewById(R.id.laEtUsername);
		pswdEt = (EditText)findViewById(R.id.laEtPassword);
		
		
		pswdEt.setOnEditorActionListener(new OnEditorActionListener() { 
		    @Override 
		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		        if (actionId == EditorInfo.IME_ACTION_DONE) {
		        	if(loginFunction()) {
		        		openSelectOption();
		        	}
		        } 
		        return false; 
		    } 
		}); 
		
		//Hide ActionBar
		//getActionBar().hide();
		
		/*
		File nfile=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath());
		if (!nfile.exists()) {
			nfile.mkdir();
			Toast.makeText( getApplicationContext(), 
							"Folder Created at "+
							Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath().toString(), 
							Toast.LENGTH_SHORT).show();
		} */

		//new parseCsvTask().execute();
		
		// Handle this on first login
		String text = DBAdapter.getUsername();
		if (text=="NILL") {
			Toast.makeText(getApplicationContext(), "Reload the file.", Toast.LENGTH_LONG).show();
		} else {
			usernameEt.setText(text);
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
		switch (item.getItemId())
        {
		/*
        case R.id.login_action_lang_english:
        	changeLanguage("en");
        	Toast.makeText(LoginActivity.this, "Default Language changed to English", Toast.LENGTH_SHORT).show();
            return true;
            
        case R.id.login_action_lang_tamil:
        	changeLanguage("ta");
		 	Toast.makeText(LoginActivity.this, R.string.tst_msg_changed_to_tamil, Toast.LENGTH_SHORT).show();
            return true;
            
        case R.id.login_action_lang_hindi:
        	changeLanguage("hi");
        	Toast.makeText(LoginActivity.this, R.string.tst_msg_changed_to_hindi, Toast.LENGTH_SHORT).show();
            return true;
          */
            
        case R.id.login_action_reload_file:
        	//new parseCsvTask().execute();

        	String ccStr = collectionClose().toString();
        	
        	if(ccStr.equals("N")) {
        		new AlertDialog.Builder(this)
  	    	   .setTitle("Warning")
  	           .setMessage("Collection not started for the File already loaded. Reload again?")
  	           .setCancelable(false)
  	           .setPositiveButton("Reload", new DialogInterface.OnClickListener() {
  	               public void onClick(DialogInterface dialog, int id) {
						Intent i = new Intent(LoginActivity.this, FileExplore.class);
						startActivity(i);
  	                    LoginActivity.this.finish(); 
  	               } 
  	           }) 
  	           .setNegativeButton("Cancel", null)
  	           .show();
        		
        	} else if (ccStr.equals("Y")) {
        		new AlertDialog.Builder(this)
 	    	   .setTitle("Warning")
 	           .setMessage("Collection not closed. Please close the collection to load the file.")
 	           .setCancelable(false)
 	           .setPositiveButton("OK", new DialogInterface.OnClickListener() {
 	               public void onClick(DialogInterface dialog, int id) {
 
 	               } 
 	           }) 
 	           .setNegativeButton("Cancel", null)
 	           .show();
        	} else {
        		Intent i = new Intent(LoginActivity.this, FileExplore.class);
    			startActivity(i);
    			this.finish();
        	}

        	return true;
        	
        case R.id.login_action_overflow:
        	return true;
 
        default:
            return super.onOptionsItemSelected(item);
        }
    }
	
	 @Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	  // TODO Auto-generated method stub
	  switch(requestCode){
	  case PICKFILE_RESULT_CODE:
	   if(resultCode==RESULT_OK){
	    String FilePath = data.getData().getPath();
	    //textFile.setText(FilePath);
	    Toast.makeText(getApplicationContext(), FilePath, Toast.LENGTH_SHORT).show();
	   }
	   break;
	   
	  }
	 }
	
	public boolean loginFunction() {
		boolean returnval = false;
		if (!pswdEt.getText().toString().isEmpty() && !usernameEt.getText().toString().isEmpty()) {
        	String un = DBAdapter.getUsername();
        	String pswd = DBAdapter.getPassword();

        	if (!un.equals(usernameEt.getText().toString()) && (!pswd.equals(pswdEt.getText().toString()))) {
        		Toast.makeText(getApplicationContext(), "Invalid Username & Password", Toast.LENGTH_LONG).show();
        		returnval = false;
        	} else if (!un.equals(usernameEt.getText().toString())) {
        		Toast.makeText(getApplicationContext(), "Invalid Username", Toast.LENGTH_LONG).show();
        		returnval = false;
        	} else if (!pswd.equals(pswdEt.getText().toString())) {
        		Toast.makeText(getApplicationContext(), "Invalid Password", Toast.LENGTH_LONG).show();
        		returnval = false;
        	} else {
        		Toast.makeText(getApplicationContext(), "Welcome "+un, Toast.LENGTH_LONG).show();
        		returnval = true;
        	}
        } else {
        	if ((usernameEt.getText().toString().isEmpty()) && (pswdEt.getText().toString().isEmpty()))
        		Toast.makeText(getApplicationContext(), "Please enter the Username & Password", Toast.LENGTH_LONG).show();
        	else if(pswdEt.getText().toString().isEmpty()) 
        		Toast.makeText(getApplicationContext(), "Please enter the Password", Toast.LENGTH_LONG).show();
        	else
        		Toast.makeText(getApplicationContext(), "Please enter the Username", Toast.LENGTH_LONG).show();
        }
		return returnval;
	}
 
	
	public void changeLanguage(String lang) {
		Locale locale = new Locale(lang); 
	    Locale.setDefault(locale);
	    Configuration config = new Configuration();
	    config.locale = locale;
	    getBaseContext().getResources().updateConfiguration(config, 
	    getBaseContext().getResources().getDisplayMetrics());
	    setContentView(R.layout.activity_login);
	}

	public void laOnClickLogin(View v) {

		if(loginFunction()) {
    		openSelectOption();
    	}
	}
	
	public void openSelectOption() {
		
		String vtemp = null;
		vtemp = collectionClose();
		if(vtemp.equals("N")) {
			Intent i = new Intent(LoginActivity.this, SelectOptionActivity.class);
			startActivity(i);
			Intent i1 = new Intent(LoginActivity.this, VehicleActivity.class);
			startActivity(i1);
			this.finish();
		} else if (vtemp.equals("Y")) {
			Intent i = new Intent(LoginActivity.this, SelectOptionActivity.class);
			startActivity(i);
			this.finish();
		} else {
			Intent i = new Intent(LoginActivity.this, SelectOptionActivity.class);
			startActivity(i);
			this.finish();
		}
	}
	
	public String collectionClose() {
		String returnV = null;
		HeaderData hd = new HeaderData();
		hd = DBAdapter.getCollectionStatus();
		
		if(hd.getCollStarted().toString().isEmpty())
			returnV = "C";
		else 	
			return hd.getCollStarted().toString();

		return returnV;
	}

	
	public void laOnClickSignup(View v) {
		
		Toast.makeText(this.getApplicationContext(), "No Signup required.", Toast.LENGTH_LONG).show();
	}
	
	//Include this code in Login Activity
		class parseCsvTask extends AsyncTask<Void, Void, String> {
			ProgressDialog pd;
			private Exception exceptionToBeThrown;

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				//Set something like processing the file
				//Optimize it to check and load the file
				pd = new ProgressDialog(LoginActivity.this);
				pd.setMessage("Loading the file..");
				pd.setIndeterminate(false);
				pd.setCancelable(false);
				pd.show();
			}

			@Override
			protected String doInBackground(Void... arg0) {
			//Record method 
				String strSuccess = "Success", strFnf = "File not found";
				int lineCount = 1;
				FileInputStream is;
				BufferedReader reader;
				//final File file = new File(Environment.getExternalStorageDirectory()+"/Data37"+"/HHM_INPUT_CSV_FILE.CSV");
				final File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/HHM_INPUT_CSV_FILE.CSV");
				
				if (file.exists()) {
					try {
						is = new FileInputStream(file);
						reader = new BufferedReader(new InputStreamReader(is));
						String line; // = reader.readLine();
						//while(line != null){
						DBAdapter.trucateTable("data_body");
						DBAdapter.trucateTable("data_header");
						DBAdapter.resetTableSequence("data_body");
						DBAdapter.resetTableSequence("data_header");
						
						do {
							line = reader.readLine();
								if(lineCount==1)
									parseCsvHeader(line);
								else
									parseCsvBody(line);
							lineCount++;
						} while(line != null);

						reader.close();
					} catch(IOException ioe) {
						//ioe.printStackTrace();
						exceptionToBeThrown = ioe;
					} catch(Exception e) {
						exceptionToBeThrown = e;
					}
					return strSuccess+". "+lineCount+" lines processed Successfully";
				} else {
					return strFnf;
				}
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				Toast.makeText(getApplicationContext(), result+"\n"+ "Rows processed :"+DBAdapter.getUserDataCount(), Toast.LENGTH_LONG).show();
				if (pd != null) {
					pd.dismiss();
				}
				if (exceptionToBeThrown != null) {
					//Show some pop-up saying Error while loading the File
					
				}
			}
		}

		public void parseCsvHeader(String csvline) {
			//Parse Header
			//String	seperatorStr = ",";
			String  usernameStr, usernameStr1, financeStr, areaStr, tempStr, filenameStr, insertedonStr;
			int 	passwordInt, passwordInt1;
			
			//usernameStr = getStringAtPosition(csvline, seperatorStr, 1);
			usernameStr = getStringAtPosition(csvline, 0);
			passwordInt	= Integer.parseInt( getStringAtPosition(csvline, 1) );

			usernameStr1= getStringAtPosition(csvline, 2);
			passwordInt1= Integer.parseInt( getStringAtPosition(csvline, 3) );
			
			financeStr 	= getStringAtPosition(csvline, 4);
			areaStr 	= getStringAtPosition(csvline, 5);
			
			//tempstr is the footer i.e mobile no
			tempStr 	= getStringAtPosition(csvline, 6);
			
			filenameStr	= getStringAtPosition(csvline, 7);
			
			//insertedonStr = DateFormat.getDateTimeInstance().format(new Date());
			insertedonStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
		        
			//Load this into the SQLite Header table
			
			DBAdapter.addHeaderData(new HeaderData(passwordInt, passwordInt1, usernameStr, usernameStr1, financeStr, areaStr, tempStr, filenameStr, null, 0, 0, "N", insertedonStr, 0));
		}

		public void parseCsvBody(String csvline) {
			//Parse Body
			String 	nameStr, mobileStr, dateStr;
			int 	accountnoInt, outstandingInt, currentdueInt;
			
			accountnoInt 	= Integer.parseInt(getStringAtPosition(csvline, 0) );
			nameStr			= getStringAtPosition(csvline, 1);
			mobileStr		= getStringAtPosition(csvline, 2);
			dateStr			= getStringAtPosition(csvline, 3);
			outstandingInt	= Integer.parseInt(getStringAtPosition(csvline, 4) );
			currentdueInt	= Integer.parseInt(getStringAtPosition(csvline, 5) );
			
			//Load this into the SQLite Detail table

			DBAdapter.addBodyData(new BodyData(accountnoInt, outstandingInt, currentdueInt, 0, nameStr, mobileStr, dateStr));

		}

		//public String getStringAtPosition(String line,String seperator,int position) {
		public String getStringAtPosition(String line, int position) {
			//Code in a way to get the String the position
			int cPos;
				for(int i=0; i<position; i++) {
					//cPos = line.indexOf(seperator);
					cPos = line.indexOf(",");
					line = line.substring(cPos+1);
				}
				cPos = line.indexOf(",");
				if(cPos>0)
				return line.substring(0, cPos);
				else
				return line;
		}

		public int getAppValidity() {
			String appValidityStr = "likecorp.app37.validity";
			//App validity check
			SharedPreferences prefs = this.getSharedPreferences("com.likecorp.app37", Context.MODE_PRIVATE);
			long currentTimeLong	= new Date().getTime(); 
			long appValidityLong	= prefs.getLong(appValidityStr, currentTimeLong);  
			
			if(currentTimeLong==appValidityLong) {
				long CurrentTime = new Date().getTime();
			}
			
			
			return 0;
		}
		
}
