package com.likecorp.app37;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.example.app37.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class VehicleActivity extends Activity {
	
	EditText etVehiclNo, etStartKm, etEndKm, etOtherExpenses;
	String upcolstat = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vehicle);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		DBAdapter.init(this);
		
		etVehiclNo 	= (EditText)findViewById(R.id.vaEtVehicleNo);
		etStartKm  	= (EditText)findViewById(R.id.vaEtStartKm);
		etEndKm	   	= (EditText)findViewById(R.id.vaEtEndKm);
		etOtherExpenses = (EditText)findViewById(R.id.vaEtOtherExpenses);
		
		onCreateDoit();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.vehicle, menu);
		return true;
	}
	
	public void savebtnOnclick(View v) {
		String colstat = null;
		int dataCnt = DBAdapter.getUserDataCount();
		
		if(dataCnt>0) {
			
			if(etStartKm.isEnabled()) {
				colstat = "N";
				upcolstat = "Y";
			}
			else if (etEndKm.isEnabled()) {
				colstat = "Y";
				upcolstat = "C";
			}
			else
				colstat = "C";
		
			if (etVehiclNo.getText().toString().isEmpty()) 
				Toast.makeText(getApplicationContext(), "Please enter vehicle no", Toast.LENGTH_SHORT).show();
			else if(colstat.equals("N") && etStartKm.getText().toString().isEmpty())
				Toast.makeText(getApplicationContext(), "Please enter Start KM", Toast.LENGTH_SHORT).show();
			else if(colstat.equals("Y") && etEndKm.getText().toString().isEmpty())
				Toast.makeText(getApplicationContext(), "Please enter End KM", Toast.LENGTH_SHORT).show();
			else if(colstat.equals("C"))
				Toast.makeText(getApplicationContext(), "Collection closed", Toast.LENGTH_SHORT).show();
			else if(colstat.equals("Y") && Integer.parseInt(etStartKm.getText().toString()) >= Integer.parseInt(etEndKm.getText().toString()))
				Toast.makeText(getApplicationContext(), "End KM should be greater than Start KM", Toast.LENGTH_SHORT).show();
			else if (colstat.equals("N")) {
				if(DBAdapter.saveCollectionHeader(etVehiclNo.getText().toString(), Integer.parseInt(etStartKm.getText().toString()), Integer.parseInt(etEndKm.getText().toString()), upcolstat, Integer.parseInt(etOtherExpenses.getText().toString()))) {
					hideKeyboard();
					finish();
					Toast.makeText(getApplicationContext(), "Saved successfully", Toast.LENGTH_SHORT).show();
				} else
					Toast.makeText(getApplicationContext(), "Error while saving.", Toast.LENGTH_SHORT).show(); 
			}
			else {
				final AlertDialog.Builder adb = new AlertDialog.Builder(this);
				
				adb.setTitle("Close Collection");
				adb.setMessage("Are you sure want to close the collection?");
				adb.setNegativeButton("Cancel", null);
				adb.setPositiveButton("OK", new AlertDialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int arg1) {
						// TODO Auto-generated method stub
						if(DBAdapter.saveCollectionHeader(etVehiclNo.getText().toString(), Integer.parseInt(etStartKm.getText().toString()), Integer.parseInt(etEndKm.getText().toString()), upcolstat, Integer.parseInt(etOtherExpenses.getText().toString()))) {
							hideKeyboard();
							etEndKm.setEnabled(false);
							etOtherExpenses.setEnabled(false);
							//finish();
							new generateCsvTask().execute();
							Toast.makeText(getApplicationContext(), "Collection closed successfully", Toast.LENGTH_SHORT).show();
						}
						else
							Toast.makeText(getApplicationContext(), "Error while saving.", Toast.LENGTH_SHORT).show(); 
					}
				});
				adb.show();	
			} 
		} else 
			Toast.makeText(getApplicationContext(), "Cann't proceed. No Collection data found. Please reoad the file.", Toast.LENGTH_SHORT).show();
	}
	
	public void onCreateDoit() {
		HeaderData hd = new HeaderData();
		
		hd = DBAdapter.getCollectionStatus();
		if(hd.getCollStarted().equals("Y")) {
			etVehiclNo.setText(hd.getVehicleNo());
			etStartKm.setText(String.valueOf(hd.getStartKm()));
			etEndKm.setText(String.valueOf(hd.getEndKm()));
			etOtherExpenses.setText(String.valueOf(hd.getOtherExpenses()));
			etVehiclNo.setEnabled(false);
			etStartKm.setEnabled(false);
		} else if (hd.getCollStarted().equals("N")) {
			etStartKm.setText(String.valueOf(hd.getStartKm()));
			etEndKm.setText(String.valueOf(hd.getEndKm()));
			etOtherExpenses.setText(String.valueOf(hd.getOtherExpenses()));
			etEndKm.setEnabled(false);
			etOtherExpenses.setEnabled(false);
		} else {
			etVehiclNo.setText(hd.getVehicleNo());
			etStartKm.setText(String.valueOf(hd.getStartKm()));
			etEndKm.setText(String.valueOf(hd.getEndKm()));
			etOtherExpenses.setText(String.valueOf(hd.getOtherExpenses()));
			etVehiclNo.setEnabled(false);
			etStartKm.setEnabled(false);
			etEndKm.setEnabled(false);
			etOtherExpenses.setEnabled(false);
		}
	}
	
	
	public void openKeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
	}
	
	public void hideKeyboard() {
		View view = this.getCurrentFocus();
		if (view != null) { 
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}
	
	class generateCsvTask extends AsyncTask<Void, Void, String> {
		ProgressDialog pd;
		private Exception exceptionToBeThrown;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			//Set something like processing the file
			//Optimize it to check and load the file
			pd = new ProgressDialog(VehicleActivity.this);
			pd.setMessage("Please wait...");
			pd.setIndeterminate(false);
			pd.setCancelable(true);
			pd.show();
		}

		@Override
		protected String doInBackground(Void... arg0) {
		//Record method 
			String Vsavedon = new SimpleDateFormat("ddMMMyyyy").format(Calendar.getInstance().getTime());
			
			//final File file = new File(Environment.getExternalStorageDirectory()+"/Data37"+"/HHM_INPUT_CSV_FILE.CSV");
			final File foldername = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "App37");
			
			if (!foldername.exists()) {
	            foldername.mkdirs();
	        }
			
			HeaderData hd = new HeaderData();
			
			hd = DBAdapter.getCollectionStatus();
			
			final String filename = hd.getFilename().toString() + Vsavedon.toUpperCase()+".CSV";
			
			File gpxfile = new File(foldername, filename);
			
			if (foldername.exists()) {
				try {
					
					FileWriter fw = new FileWriter(gpxfile);
					BufferedWriter bw = new BufferedWriter(fw);
					PrintWriter pw = new PrintWriter(bw);
					

					pw.append(hd.getUsername() + ",");
					pw.append(hd.getPassword()  + ",");
					pw.append(hd.getUsername1()  + ",");
					pw.append(hd.getPassword1()  + ",");
					pw.append(hd.getFinance()  + ",");
					pw.append(hd.getArea() + ",");
					pw.append(hd.getTemp() + ",");
					pw.append(hd.getFilename() + ",");
					
					//pw.append("\n");
					pw.println("\r");
					
					pw.append(hd.getVehicleNo() + ",");
					pw.append(hd.getStartKm() + ",");
					pw.append(hd.getEndKm() + ",");
					pw.append(hd.getOtherExpenses() + ",");
					
					//pw.append("\n");
					pw.println("\r");
					
					Cursor c = DBAdapter.getAllDataBody();
					if(c != null && c.moveToFirst() ) {
						if(c.moveToFirst()) {
							do{
								pw.append(String.valueOf(c.getInt(c.getColumnIndex(DBAdapter.DATA_BODY_KEY_ACC_NO)))  + ",");
								pw.append(c.getString(c.getColumnIndex(DBAdapter.DATA_BODY_KEY_CUST_NAME))  + ",");
								pw.append(c.getString(c.getColumnIndex(DBAdapter.DATA_BODY_KEY_MOBILE))  + ",");
								pw.append(c.getString(c.getColumnIndex(DBAdapter.DATA_BODY_KEY_OPEN_DATE))  + ",");
								pw.append(String.valueOf(c.getInt(c.getColumnIndex(DBAdapter.DATA_BODY_KEY_OUT_STAND)))  + ",");
								pw.append(String.valueOf(c.getInt(c.getColumnIndex(DBAdapter.DATA_BODY_KEY_CURR_DUE)))  + ",");
								pw.append(String.valueOf(c.getInt(c.getColumnIndex(DBAdapter.DATA_BODY_KEY_PAID_DUE)))  + ",");
								pw.append(String.valueOf(  	c.getInt(c.getColumnIndex(DBAdapter.DATA_BODY_KEY_OUT_STAND)) - 
															c.getInt(c.getColumnIndex(DBAdapter.DATA_BODY_KEY_PAID_DUE))  )  + ",");
								pw.append(c.getString(c.getColumnIndex(DBAdapter.DATA_BODY_KEY_SAVEDON)) + ",");
								pw.append(c.getString(c.getColumnIndex(DBAdapter.DATA_BODY_KEY_ACCCLOSED)) + ",");
								pw.println("\r");
								
							} while(c.moveToNext());
						}
					}
					
					
					//pw.flush();
					pw.close();

				} catch(IOException ioe) {
					exceptionToBeThrown = ioe;
				} catch(Exception e) {
					exceptionToBeThrown = e;
				}
			}
			return "File created at "+gpxfile.toString();
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
			if (pd != null) {
				pd.dismiss();
				finish();
			}
			if (exceptionToBeThrown != null) {
				//Show some pop-up saying Error while loading the File
				
			}
		}
	}

}
