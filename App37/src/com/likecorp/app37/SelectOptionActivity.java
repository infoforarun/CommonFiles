package com.likecorp.app37;

import com.example.app37.R;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class SelectOptionActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_option);
		DBAdapter.init(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.select_option, menu);
		return true;
	}
	
	@Override 
	public void onBackPressed() { 
	    new AlertDialog.Builder(this)
	    	   .setTitle("Exit")
	           .setMessage("Are you sure you want to exit?")
	           .setCancelable(false)
	           .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	                    SelectOptionActivity.this.finish(); 
	               } 
	           }) 
	           .setNegativeButton("Cancel", null)
	           .show();
	} 
	
	public void asoOnClickLoanDetails(View v) {
		
		int dataCnt = DBAdapter.getUserDataCount();
		String vtemp = null;
		
		
		if (dataCnt>0) {
				vtemp = collectionClose();
			if(vtemp.equals("Y")) {
				Intent i = new Intent(SelectOptionActivity.this, MainActivity.class);
				startActivity(i);
			} else if (vtemp.equals("N"))
				Toast.makeText(getApplicationContext(), "Start the Collection by entering vehicle details", Toast.LENGTH_SHORT).show();
			else 
				Toast.makeText(getApplicationContext(), "Cann't proceed. Collection closed.", Toast.LENGTH_SHORT).show();
		} else
			Toast.makeText(getApplicationContext(), "Cann't proceed. No Collection data found. Please reoad the file.", Toast.LENGTH_SHORT).show();
		
	}
	
	public void asoOnClickReport(View v) {
		Intent i = new Intent(SelectOptionActivity.this, ReportActivity.class);
		startActivity(i);
	}
	
	public void asoOnClickVehicleDetails(View v) {
		Intent i = new Intent(SelectOptionActivity.this, VehicleActivity.class);
		startActivity(i);
	}
	
	public void asoOnClickSettings(View v) {
		Toast.makeText(getApplicationContext(), "Welcome", Toast.LENGTH_SHORT).show();
	}
	
	public void asoOnClickGenCsv(View v) {

			//new generateCsvTask().execute();
	}
	
	public String collectionClose() {
		HeaderData hd = new HeaderData();
		hd = DBAdapter.getCollectionStatus();
		
		return hd.getCollStarted().toString();

	}


}
