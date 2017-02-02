package com.likecorp.app37;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import com.example.app37.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class FileExplore extends Activity {
	
	String filepath = null;

	// Stores names of traversed directories
	ArrayList<String> str = new ArrayList<String>();

	// Check if the first level of the directory structure is the one showing
	private Boolean firstLvl = true;

	private static final String TAG = "F_PATH";

	private Item[] fileList;
	//private File path = new File(Environment.getExternalStorageDirectory() + "");
	private File path = new File("/storage");
	private String chosenFile;
	private static final int DIALOG_LOAD_FILE = 1000;

	ListAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		//getActionBar().setDisplayHomeAsUpEnabled(true);
		DBAdapter.init(this);

		loadFileList();

		showDialog(DIALOG_LOAD_FILE);
		Log.d(TAG, path.getAbsolutePath());

	}
	
	@Override 
	public void onBackPressed() 
	{ 
	    finish(); 
	    Intent i = new Intent(FileExplore.this, LoginActivity.class);
		startActivity(i);
	} 

	private void loadFileList() {
		try {
			path.mkdirs();
		} catch (SecurityException e) {
			Log.e(TAG, "unable to write on the sd card ");
		}

		// Checks whether path exists
		if (path.exists()) {
			FilenameFilter filter = new FilenameFilter() {
				@Override
				public boolean accept(File dir, String filename) {
					File sel = new File(dir, filename);
					// Filters based on whether the file is hidden or not
					return (sel.isFile() || sel.isDirectory())
							&& !sel.isHidden();

				}
			};

			String[] fList = path.list(filter);
			fileList = new Item[fList.length];
			for (int i = 0; i < fList.length; i++) {
				fileList[i] = new Item(fList[i], R.drawable.file_icon);

				// Convert into file path
				File sel = new File(path, fList[i]);

				// Set drawables
				if (sel.isDirectory()) {
					fileList[i].icon = R.drawable.directory_icon;
					Log.d("DIRECTORY", fileList[i].file);
				} else {
					Log.d("FILE", fileList[i].file);
				}
			}

			if (!firstLvl) {
				Item temp[] = new Item[fileList.length + 1];
				for (int i = 0; i < fileList.length; i++) {
					temp[i + 1] = fileList[i];
				}
				temp[0] = new Item("Up", R.drawable.directory_up);
				fileList = temp;
			}
		} else {
			Log.e(TAG, "path does not exist");
		}

		adapter = new ArrayAdapter<Item>(this,
				android.R.layout.select_dialog_item, android.R.id.text1,
				fileList) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// creates view
				View view = super.getView(position, convertView, parent);
				TextView textView = (TextView) view
						.findViewById(android.R.id.text1);

				// put the image on the text view
				textView.setCompoundDrawablesWithIntrinsicBounds(
						fileList[position].icon, 0, 0, 0);

				// add margin between image and text (support various screen
				// densities)
				int dp5 = (int) (5 * getResources().getDisplayMetrics().density + 0.5f);
				textView.setCompoundDrawablePadding(dp5);

				return view;
			}
		};

	}

	private class Item {
		public String file;
		public int icon;

		public Item(String file, Integer icon) {
			this.file = file;
			this.icon = icon;
		}

		@Override
		public String toString() {
			return file;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		AlertDialog.Builder builder = new Builder(this);

		if (fileList == null) {
			Log.e(TAG, "No files loaded");
			dialog = builder.create();
			return dialog;
		}

		switch (id) {
		case DIALOG_LOAD_FILE:
			builder.setTitle("Choose your file");
			builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					chosenFile = fileList[which].file;
					File sel = new File(path + "/" + chosenFile);
					if (sel.isDirectory()) {
						firstLvl = false;

						// Adds chosen directory to list
						str.add(chosenFile);
						fileList = null;
						path = new File(sel + "");

						loadFileList();

						removeDialog(DIALOG_LOAD_FILE);
						showDialog(DIALOG_LOAD_FILE);
						Log.d(TAG, path.getAbsolutePath());

					}

					// Checks if 'up' was clicked
					else if (chosenFile.equalsIgnoreCase("up") && !sel.exists()) {

						// present directory removed from list
						String s = str.remove(str.size() - 1);

						// path modified to exclude present directory
						path = new File(path.toString().substring(0,
								path.toString().lastIndexOf(s)));
						fileList = null;

						// if there are no more directories in the list, then
						// its the first level
						if (str.isEmpty()) {
							firstLvl = true;
						}
						loadFileList();

						removeDialog(DIALOG_LOAD_FILE);
						showDialog(DIALOG_LOAD_FILE);
						Log.d(TAG, path.getAbsolutePath());

					}
					// File picked
					else {
						// Perform action with file picked
						filepath = path+"/"+chosenFile;
						
						String extns = getFileExt(filepath);
						
						if(extns.equals("CSV"))
							new parseCsvTask().execute(filepath);
						else {
							Toast.makeText(getApplicationContext(), "Invalid file. \n" + filepath, Toast.LENGTH_SHORT).show();
							Intent i = new Intent(FileExplore.this, LoginActivity.class);
							startActivity(i);
							finish();
						}
					}

				}
			});
			break;
		}
		dialog = builder.show();
		return dialog;
	}


	public static String getFileExt(String fileName) {      
		String extn = fileName.substring((fileName.lastIndexOf(".") + 1), fileName.length());
	   return extn.toUpperCase();
	} 
	
	
	
	/*				*/
	
	//Include this code in Login Activity
	class parseCsvTask extends AsyncTask<String, Void, String> {
		ProgressDialog pd;
		private Exception exceptionToBeThrown;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			//Set something like processing the file
			//Optimize it to check and load the file
			pd = new ProgressDialog(FileExplore.this);
			pd.setMessage("Loading the file..");
			pd.setIndeterminate(false);
			pd.setCancelable(false);
			pd.show();
		}

		@Override
		protected String doInBackground(String... arg) {
		//Record method 
			String strSuccess = "Success", strFnf = "File not found";
			int lineCount = 1;
			FileInputStream is;
			BufferedReader reader;
			//final File file = new File(Environment.getExternalStorageDirectory()+"/Data37"+"/HHM_INPUT_CSV_FILE.CSV");
			//final File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/HHM_INPUT_CSV_FILE.CSV");
			
			File file = new File(arg[0]);
			
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
			Toast.makeText(getApplicationContext(), DBAdapter.getUserDataCount() + " Users imported", Toast.LENGTH_LONG).show();
			if (pd != null) {
				pd.dismiss();
				Intent i = new Intent(FileExplore.this, LoginActivity.class);
				startActivity(i);
				finish();
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
		int 	passwordInt, passwordInt1, tempInt;
		
		String csvHValues[] = csvline.split(",", 8);
		
		usernameStr = csvHValues[0].toString();
		passwordInt	= Integer.parseInt( csvHValues[1].toString() );

		usernameStr1= csvHValues[2].toString();
		passwordInt1= Integer.parseInt( csvHValues[3].toString() );
		
		financeStr 	= csvHValues[4].toString();
		areaStr 	= csvHValues[5].toString();
		
		//tempstr is the footer i.e mobile no
		tempStr 	= csvHValues[6].toString();
		
		filenameStr	= csvHValues[7].toString();
		
		/*
		usernameStr = getStringAtPosition(csvline, 0);
		passwordInt	= Integer.parseInt( getStringAtPosition(csvline, 1) );

		usernameStr1= getStringAtPosition(csvline, 2);
		passwordInt1= Integer.parseInt( getStringAtPosition(csvline, 3) );
		
		financeStr 	= getStringAtPosition(csvline, 4);
		areaStr 	= getStringAtPosition(csvline, 5);
		
		//tempstr is the footer i.e mobile no
		tempStr 	= getStringAtPosition(csvline, 6);
		
		filenameStr	= getStringAtPosition(csvline, 7);
		*/
		
		//insertedonStr = DateFormat.getDateTimeInstance().format(new Date());
		insertedonStr = new SimpleDateFormat("yyMMddHHmmss").format(Calendar.getInstance().getTime());
	        
		//Load this into the SQLite Header table
		
		DBAdapter.addHeaderData(new HeaderData(passwordInt, passwordInt1, usernameStr, usernameStr1, financeStr, areaStr, tempStr, filenameStr, null, 0, 0, "N", insertedonStr, 0));
	}

	public void parseCsvBody(String csvline) {
		//Parse Body
		String 	nameStr, mobileStr, dateStr;
		int 	accountnoInt, outstandingInt, currentdueInt;
		
		String csvValues[] = csvline.split(",", 6);
		
		accountnoInt 	= Integer.parseInt(csvValues[0].toString());
		nameStr			= csvValues[1].toString();
		mobileStr		= csvValues[2].toString();
		dateStr			= csvValues[3].toString();
		outstandingInt	= Integer.parseInt(csvValues[4].toString());
		currentdueInt	= Integer.parseInt(csvValues[5].toString());
		
		/*
		accountnoInt 	= Integer.parseInt(getStringAtPosition(csvline, 0) );
		nameStr			= getStringAtPosition(csvline, 1);
		mobileStr		= getStringAtPosition(csvline, 2);
		dateStr			= getStringAtPosition(csvline, 3);
		outstandingInt	= Integer.parseInt(getStringAtPosition(csvline, 4) );
		currentdueInt	= Integer.parseInt(getStringAtPosition(csvline, 5) );
		*/
		
		//Load this into the SQLite Detail table

		DBAdapter.addBodyData(new BodyData(accountnoInt, outstandingInt, currentdueInt, 0, nameStr, mobileStr, dateStr));

	}

	
	//public String getStringAtPosition(String line,String seperator,int position) {
	/*
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
			// else if(line.length()>10)
			// return line.substring(0, cPos); 
			else
			return line;
	} 
	*/
	
	
}