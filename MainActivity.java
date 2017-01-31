package com.likecorp.app37;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.example.app37.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ParseException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	EditText accnoEt, nameEt, mobileEt, odEt, noofdaysEt, loanAmtEt, currDueEt, paidDueEt;
	Button nextBtn, prevBtn;
	ImageButton clrPaidDueImgBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_main);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		accnoEt		= (EditText)findViewById(R.id.maEtAccountNo);
		nameEt 		= (EditText)findViewById(R.id.maEtName);
		mobileEt	= (EditText)findViewById(R.id.maEtMobile);
		odEt		= (EditText)findViewById(R.id.maEtOpeningDate);
		noofdaysEt	= (EditText)findViewById(R.id.maEtNoOfDays);
		loanAmtEt	= (EditText)findViewById(R.id.maEtLoanAmount);
		currDueEt	= (EditText)findViewById(R.id.maEtCurrentDue);
		paidDueEt	= (EditText)findViewById(R.id.maEtPaidDue);
		nextBtn		= (Button)findViewById(R.id.maBtnNext);
		prevBtn		= (Button)findViewById(R.id.maBtnPrev);
		
		clrPaidDueImgBtn = (ImageButton)findViewById(R.id.maIbClearPaidDue);
		
		DBAdapter.init(this);
		
		BodyData data = new BodyData();
		data = DBAdapter.getCurrentDataBody();

		populateFields(data);
		
		//Toast.makeText(getApplicationContext(), data.getAccountno(), Toast.LENGTH_LONG).show();
		/*
		paidDueEt.setOnClickListener(new View.OnClickListener() { 
			 
		    @Override 
		    public void onClick(View v) {
		    	paidDueEt.selectAll();// setSelection(paidDueEt.getText().length()-1, 0); 
		    } 
		});
		*/
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void clearAllFields() {
		accnoEt.setText("");
		nameEt.setText(""); 
		mobileEt.setText("");
		odEt.setText("");
		noofdaysEt.setText("");
		loanAmtEt.setText("");
		currDueEt.setText("");
	}
	
	public void nextbtnOnclick(View v) {
		nextbtnFunction();
		hideKeyboard();
		paidDueEt.selectAll();
	}
	
	public void nextbtnFunction() {
		BodyData data1 = new BodyData();
		data1 = DBAdapter.getNextDataBody();

		if(data1.getEnaDisable()) {
			clearAllFields();
			populateFields(data1);
		} else {
			nextBtn.setEnabled(false);
			prevBtn.setEnabled(true);
			paidDueEt.setEnabled(false);
		}
	}
	
	public void prevbtnOnclick(View v) {
		prevbtnFunction();
		hideKeyboard();
	}
	
	public void prevbtnFunction() {
		BodyData data1 = new BodyData();
		data1 = DBAdapter.getPrevDataBody();

		if(data1.getEnaDisable()) {
			clearAllFields();
			populateFields(data1);
		} else {
			//prevBtn.setEnabled(false);
			//prevBtn.setBackgroundColor(getResources().getColor(R.color.bg_button_disable));
			nextBtn.setEnabled(true);
		}
	}
	
	public void savebtnOnClick(View v) {
		
		BodyData bd = new BodyData();
		bd = DBAdapter.getCurrentDataBody();
		
		if(!bd.getPaidStatus()) {
		
			AlertDialog.Builder closeDialg = new AlertDialog.Builder(this); 
			
			closeDialg.setTitle("Confirm");
			closeDialg.setMessage("Are you sure want to close the account?");
			closeDialg.setNegativeButton("Cancel", null);
			closeDialg.setCancelable(false);
			closeDialg.setPositiveButton("Close", new AlertDialog.OnClickListener() {
				public void onClick(DialogInterface dialog, int arg1) {
					int paidDueV = Integer.parseInt(paidDueEt.getText().toString());
					
					String str = DBAdapter.saveBodyData(Integer.parseInt(accnoEt.getText().toString()), paidDueV);
					
					if (str.equals("Saved successfully.")) {
						nextbtnFunction();
						hideKeyboard();
						paidDueEt.selectAll();
					} else {
						Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
					}
				}
			});
			
		    // create alert dialog 
		    final AlertDialog alertDialogSuccess = closeDialg.create();
		    
			
			if (!paidDueEt.getText().toString().isEmpty()) {
				final AlertDialog.Builder saveDialg = new AlertDialog.Builder(this);
				
				saveDialg.setTitle("Confirm");
				saveDialg.setMessage(	accnoEt.getText().toString() +
										"-" +
										nameEt.getText().toString() +
										" Paid Rs."+ 
										paidDueEt.getText().toString() + 
										"\nAre you sure want to save?");
				saveDialg.setNegativeButton("Cancel", null);
				saveDialg.setCancelable(false);
				saveDialg.setPositiveButton("Save", new AlertDialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int arg1) {
						// TODO Auto-generated method stub
						int paidDueV = Integer.parseInt(paidDueEt.getText().toString());
						int loanAmtV = Integer.parseInt(loanAmtEt.getText().toString());
						
						if(loanAmtV>0 && paidDueV>=loanAmtV ) {
							alertDialogSuccess.show();
						}
						else {
							String str = DBAdapter.saveBodyData(Integer.parseInt(accnoEt.getText().toString()), paidDueV);
						
							if (str.equals("Saved successfully.")) {
								nextbtnFunction();
								hideKeyboard();
								paidDueEt.selectAll();
							} else {
								Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
							}
						}
					
					}
				});
				saveDialg.show();	
					
			} else {
				Toast.makeText(getApplicationContext(), "Enter Paid Due to save", Toast.LENGTH_SHORT).show();
			}
		}
		else {
			Toast.makeText(getApplicationContext(), "Data already saved", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void clearIbOnClick(View v) {
		if(paidDueEt.isEnabled()) {
			paidDueEt.setText("");
			paidDueEt.requestFocus();
			openKeyboard();
		}
	}
	
	public void searchIbOnClick(View v) {
		showInputDialog();
	}
	
	protected void showInputDialog() {

		// get prompts.xml view
		LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
		View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
		alertDialogBuilder.setView(promptView);

		final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
		
		/*
		editText.setOnEditorActionListener(new OnEditorActionListener() { 
		    @Override 
		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		        if (actionId == EditorInfo.IME_ACTION_DONE) {
		        	if(DBAdapter.isValidAccountNo(Integer.parseInt(editText.getText().toString()))) {
						BodyData data1 = new BodyData();
						data1 = DBAdapter.getDataBody(Integer.parseInt(editText.getText().toString()));
						populateFields(data1);
					} else {
						Toast.makeText(getApplicationContext(), "Invalid Account No", Toast.LENGTH_SHORT).show();
					}
		        } 
		        return true; 
		    } 
		});
		*/
		// setup a dialog window
		alertDialogBuilder.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						if(DBAdapter.isValidAccountNo(Integer.parseInt(editText.getText().toString()))) {
							BodyData data1 = new BodyData();
							data1 = DBAdapter.getDataBody(Integer.parseInt(editText.getText().toString()));
							populateFields(data1);
							hideKeyboard();
						} else {
							Toast.makeText(getApplicationContext(), "Invalid Account No", Toast.LENGTH_SHORT).show();
							hideKeyboard();
						}
					}
				})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});

		// create an alert dialog
		AlertDialog alert = alertDialogBuilder.create();
		alert.show();
		
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
	
	public void populateFields(BodyData bd) {
		accnoEt.setText(String.valueOf(bd.getAccountno()));
		nameEt.setText(bd.getCName());
		mobileEt.setText(String.valueOf(bd.getMobile()));
		odEt.setText(bd.getOpeningDate());
		noofdaysEt.setText(getDaysBetween(bd.getOpeningDate()));
		//noofdaysEt.setText("");
		loanAmtEt.setText(String.valueOf(bd.getOutstanding()));
		currDueEt.setText(String.valueOf(bd.getCurrentDue()));
		if(bd.getPaidStatus()) {
			paidDueEt.setEnabled(false);
			clrPaidDueImgBtn.setEnabled(false);
			paidDueEt.setText(String.valueOf(bd.getPaidDue()));
		} else {
			paidDueEt.setEnabled(true);
			clrPaidDueImgBtn.setEnabled(true);
			paidDueEt.setText(String.valueOf(bd.getCurrentDue()));
		}
		nextBtn.setEnabled(true);
	}
	
	public String getDaysBetween(String strStartDate) {
		int noOfDays = 0;
		SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");
		
		try {
			Date startDate = myFormat.parse(strStartDate);
			Date endDate = Calendar.getInstance().getTime();
			
			long diff = endDate.getTime() - startDate.getTime();
			noOfDays = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return String.valueOf(noOfDays);
	}
}
