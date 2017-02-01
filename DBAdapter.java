package com.likecorp.app37;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {
	/******** if debug is set true then it will show all Logcat message *******/ 
    public static final boolean DEBUG = true;
    
    /******************** Logcat TAG ************/ 
    public static final String LOG_TAG = "DBAdapter";
    
    /******************** Table Fields ************/ 
    public static final String DATA_BODY_KEY_ID 		= "_id";
    public static final String DATA_BODY_KEY_CUST_NAME	= "customer_name";
    public static final String DATA_BODY_KEY_MOBILE		= "mobile";
    public static final String DATA_BODY_KEY_OPEN_DATE	= "opening_date";
    public static final String DATA_BODY_KEY_ACC_NO		= "account_no";
    public static final String DATA_BODY_KEY_OUT_STAND	= "out_standing";
    public static final String DATA_BODY_KEY_CURR_DUE	= "curr_due";
    public static final String DATA_BODY_KEY_CUR_ROW	= "curr_row";
    public static final String DATA_BODY_KEY_PAID_DUE	= "paid_due";
    public static final String DATA_BODY_KEY_SAVED		= "saved";
    public static final String DATA_BODY_KEY_SAVEDON	= "savedon";
    public static final String DATA_BODY_KEY_ACCCLOSED  = "acc_closed";
    
    
    public static final String DATA_HEADER_KEY_ID 			= "_id";
    public static final String DATA_HEADER_KEY_USERNAME 	= "username";
    public static final String DATA_HEADER_KEY_PASSWORD		= "password";
    public static final String DATA_HEADER_KEY_USERNAME1 	= "username1"; 
    public static final String DATA_HEADER_KEY_PASSWORD1	= "password1";
    public static final String DATA_HEADER_KEY_FINANCE  	= "finance";
    public static final String DATA_HEADER_KEY_AREA			= "area";
    public static final String DATA_HEADER_KEY_TEMPFIELD	= "tempfield";
    public static final String DATA_HEADER_KEY_FILENAME		= "filename";
    public static final String DATA_HEADER_KEY_INSERTEDON	= "insertedon";
    public static final String DATA_HEADER_KEY_VEHICLENO	= "vehicleno";
    public static final String DATA_HEADER_KEY_STARTKM		= "startkm";
    public static final String DATA_HEADER_KEY_ENDKM		= "endkm";
    public static final String DATA_HEADER_KEY_COLLSTARTED	= "coll_started";
    public static final String DATA_HEADER_KEY_OTHEREXPENSES= "other_expense";
    
    /******************** Database Name ************/ 
    public static final String DATABASE_NAME = "DB_sqllite";
    
    /**** Database Version (Increase one if want to also upgrade your database) ***/ 
    public static final int DATABASE_VERSION = 1; // started at 1
    
    /******************** Table names   ************/ 
    public static final String DATA_BODY_TABLE = "data_body";
    public static final String DATA_HEADER_TABLE = "data_header";
    
    /******* Set all table with comma separated like USER_TABLE,ABC_TABLE *******/ 
    private static final String[ ] ALL_TABLES = { DATA_BODY_TABLE,  DATA_HEADER_TABLE};
    
    /******* Create table syntax ****/ 
    private static final String BODY_CREATE = 	"create table data_body ( _id integer primary key autoincrement, " +
	    										"customer_name text, " +
	    										"mobile text, " +
	    										"opening_date text, " +
	    										"account_no integer, " +
	    										"out_standing integer, " +
	    										"curr_due integer, " +
	    										"paid_due integer, " +
	    										"curr_row text, " +
	    										"saved text," +
	    										"savedon text, " +
	    										"acc_closed text );"; 
	    
    private static final String HEADER_CREATE = "create table data_header ( _id integer primary key autoincrement, " +
    											"username text, " +
    											"password integer, " +
    											"username1 text, " +
    											"password1 integer, " +
    											"finance text, " +
    											"area text, " +
    											"tempfield text, " +
    											"filename text, " +
    											"vehicleno text, " +
    											"startkm integer, " +
    											"endkm interger, " +
    											"coll_started text, " +
    											"insertedon text, " +
    											"other_expense integer );";

    /******************** Used to open database in syncronised way ************/ 
    private static DataBaseHelper DBHelper = null;
    
    protected DBAdapter() { 
    
    } 
    /*********** Initialize database *************/ 
    public static void init(Context context) {
        if (DBHelper == null) {
            if (DEBUG)
                Log.i("DBAdapter", context.toString());
            DBHelper = new DataBaseHelper(context);
        }
    }
    
    /********** Main Database creation INNER class ********/ 
    private static class DataBaseHelper extends SQLiteOpenHelper {
        public DataBaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        } 
  
        @Override 
        public void onCreate(SQLiteDatabase db) {
            if (DEBUG)
                Log.i(LOG_TAG, "new create");
            try { 
                db.execSQL(BODY_CREATE);
                db.execSQL(HEADER_CREATE);
                
            } catch (Exception exception) {
                if (DEBUG)
                    Log.i(LOG_TAG, "Exception onCreate() exception");
            }
        }
  
        @Override 
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (DEBUG)
                Log.w(LOG_TAG, "Upgrading database from version" + oldVersion + "to" + newVersion + "...");
            	
            for (String table : ALL_TABLES) {
                db.execSQL("DROP TABLE IF EXISTS " + table);
            }
            onCreate(db);
        }

    } // Inner class closed 
      

    /***** Open database for insert,update,delete in syncronized manner ****/ 
    private static synchronized SQLiteDatabase open() throws SQLException {
        return DBHelper.getWritableDatabase();
    }
    
    /********** Escape string for single quotes (Insert,Update) *******/ 
    private static String sqlEscapeString(String aString) {
        String aReturn = "";
          
        if (null != aString) {
            //aReturn = aString.replace(", ); 
            aReturn = DatabaseUtils.sqlEscapeString(aString);
            // Remove the enclosing single quotes ... 
            aReturn = aReturn.substring(1, aReturn.length() - 1);
        } 
          
        return aReturn;
    } 
    
    public static void addBodyData(BodyData bData) {
    	  
        // Open database for Read / Write        
  
        final SQLiteDatabase db = open();

        String Vcname = sqlEscapeString(bData.getCName());
        String Vmobile = sqlEscapeString(bData.getMobile());
        String Vopeningdate = sqlEscapeString(bData.getOpeningDate());
        String Vsavedon = null;
        //int Vid = bData.getId();
        int Vaccountno = bData.getAccountno();
        int Voutstanding = bData.getOutstanding();
        int Vcurrdue = bData.getCurrentDue();
        
        ContentValues cVal = new ContentValues();
        
        cVal.put(DATA_BODY_KEY_CUST_NAME, Vcname);
        cVal.put(DATA_BODY_KEY_MOBILE, Vmobile);
        cVal.put(DATA_BODY_KEY_OPEN_DATE, Vopeningdate);
        cVal.put(DATA_BODY_KEY_ACC_NO, Vaccountno);
        cVal.put(DATA_BODY_KEY_OUT_STAND, Voutstanding);
        cVal.put(DATA_BODY_KEY_CURR_DUE, Vcurrdue);
        cVal.put(DATA_BODY_KEY_PAID_DUE, 0);
        cVal.put(DATA_BODY_KEY_CUR_ROW, "N");
        cVal.put(DATA_BODY_KEY_SAVED, "N");
        cVal.put(DATA_BODY_KEY_ACCCLOSED, "");
        
        Vsavedon = new SimpleDateFormat("yyMMddHHmmss").format(Calendar.getInstance().getTime());
        cVal.put(DATA_BODY_KEY_SAVEDON, Vsavedon);
        
        // Insert user values in database 
        db.insert(DATA_BODY_TABLE, null, cVal);     
        db.close(); // Closing database connection
    }
    
    public static void addHeaderData(HeaderData hData) {
    	
    	final SQLiteDatabase db = open();
    	
    	String Vusername 	= sqlEscapeString(hData.getUsername());
    	int    Vpassword 	= hData.getPassword();
    	String Vusername1 	= sqlEscapeString(hData.getUsername1());
    	int    Vpassword1 	= hData.getPassword1();
    	int    Vstartkm		= hData.getStartKm();
    	int    Vendkm		= hData.getEndKm();    	
    	
    	String vFinance		= sqlEscapeString(hData.getFinance());
    	String vArea		= sqlEscapeString(hData.getArea());
    	String vTempStr		= sqlEscapeString(hData.getTemp());
    	String vFile		= sqlEscapeString(hData.getFilename());
    	String vInsetedOn	= sqlEscapeString(hData.getInsertedon());
    	String vVehicleno   = sqlEscapeString(hData.getVehicleNo());
    	String vCollStatus	= sqlEscapeString(hData.getCollStarted());
    	
    	
    	ContentValues cVal = new ContentValues();
    	
    	cVal.put(DATA_HEADER_KEY_USERNAME, Vusername);
		cVal.put(DATA_HEADER_KEY_PASSWORD, Vpassword);
		cVal.put(DATA_HEADER_KEY_USERNAME1, Vusername1);
		cVal.put(DATA_HEADER_KEY_PASSWORD1, Vpassword1);
		cVal.put(DATA_HEADER_KEY_FINANCE, vFinance);
		cVal.put(DATA_HEADER_KEY_AREA, vArea);
		cVal.put(DATA_HEADER_KEY_TEMPFIELD, vTempStr);
		cVal.put(DATA_HEADER_KEY_FILENAME, vFile);
		cVal.put(DATA_HEADER_KEY_COLLSTARTED, vCollStatus);
		cVal.put(DATA_HEADER_KEY_STARTKM, Vstartkm);
		cVal.put(DATA_HEADER_KEY_ENDKM, Vendkm);
		cVal.put(DATA_HEADER_KEY_INSERTEDON, vInsetedOn);
    	
		long cnt = db.insert(DATA_HEADER_TABLE, null, cVal);
    	db.close();
    	
    }
  
    public static void trucateTable(String table) {
    	
    	final SQLiteDatabase db = open();
    	
    	db.delete(table, null, null);
    	db.close();
    }
    
    
    public static void resetTableSequence(String table) {
    	
    	final SQLiteDatabase db = open();
    
    	db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + table + "'");
    	db.close();
    }
    
    public static int getUserDataCount() { 
    	  
        final SQLiteDatabase db = open();
     
        String countQuery = "SELECT  * FROM " + DATA_BODY_TABLE;
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        // return count 
        return count;
    }
    
    /* Implement this in next phase */
    public static BodyData getCurrentDataBody() {	
    	
    	final SQLiteDatabase db = open();

    	String anyCurrRow = "SELECT COUNT(*) AS CNT FROM " + DATA_BODY_TABLE + " WHERE CURR_ROW = \"C\" ";
        Cursor c = db.rawQuery(anyCurrRow, null);
        
        if( c != null && c.moveToFirst() ){
	        if( c.getInt(0) == 0 || c.getInt(0) > 1) {
	    		ContentValues values1 = new ContentValues();
	    		values1.put(DATA_BODY_KEY_CUR_ROW, "N");
	    		db.update(DATA_BODY_TABLE, values1, null, null);
	    		// Update the first index as C
	    		ContentValues values = new ContentValues();
	    		values.put(DATA_BODY_KEY_CUR_ROW, "C");
	        	
	    		int cnt = db.update(DATA_BODY_TABLE, values, DATA_BODY_KEY_ID + " =?", new String[] { String.valueOf(1)} );
	    	}
	        c.close();
        }
        
    	String rowData = "SELECT * FROM " + DATA_BODY_TABLE + " WHERE CURR_ROW = 'C' ";
    	
    	Cursor c1 = db.rawQuery(rowData, null);
    	
    	BodyData bd = new BodyData();
    	
    	if(c1 != null && c1.moveToFirst() ) {
	    	bd.setAccountno(c1.getInt(c1.getColumnIndex(DATA_BODY_KEY_ACC_NO)));
	    	bd.setCName(c1.getString(c1.getColumnIndex(DATA_BODY_KEY_CUST_NAME)));
	    	bd.setCurrentDue(c1.getInt(c1.getColumnIndex(DATA_BODY_KEY_CURR_DUE)));
	    	bd.setId(c1.getInt(c1.getColumnIndex(DATA_BODY_KEY_ID)));
	    	bd.setMobile(c1.getString(c1.getColumnIndex(DATA_BODY_KEY_MOBILE)));
	    	bd.setOpeningDate(c1.getString(c1.getColumnIndex(DATA_BODY_KEY_OPEN_DATE)));
	    	bd.setOutstanding(c1.getInt(c1.getColumnIndex(DATA_BODY_KEY_OUT_STAND)));
	    	bd.setPaidDue(c1.getInt(c1.getColumnIndex(DATA_BODY_KEY_PAID_DUE)));
	    	bd.setPaidDue(c1.getInt(c1.getColumnIndex(DATA_BODY_KEY_PAID_DUE)));
	    	if((c1.getString(c1.getColumnIndex(DATA_BODY_KEY_SAVED))).equals("Y")) {
        		bd.setPaidStatus(true);
        	} else {
        		bd.setPaidStatus(false);
        	}
    	}
    	
    	c1.close();
    	
    	db.close();
    	return bd;
    }
    
    public static BodyData getNextDataBody() {    	
    	
    	final SQLiteDatabase db = open();
    	int maxVal = 0, currVal = 0;
    	
    	String anyCurrRow = "SELECT max(_id) AS maxid FROM " + DATA_BODY_TABLE;
        Cursor c = db.rawQuery(anyCurrRow, null);
        
        if( c != null && c.moveToFirst() ){
	        maxVal = c.getInt(0);
        }
        c.close();
        
        String currRow = "SELECT * FROM " + DATA_BODY_TABLE + " WHERE CURR_ROW = \"C\" ";
        Cursor c1 = db.rawQuery(currRow, null);
        
        if( c1 != null && c1.moveToFirst() ){
	        currVal = c1.getInt(c1.getColumnIndex(DATA_BODY_KEY_ID));
        }
        
        c1.close();
        
        BodyData data = new BodyData();
        
        if(maxVal > 0 && maxVal == currVal) {
        	data.setEnaDisable(false);  // Disable
        } else {
        	
        	ContentValues values1 = new ContentValues();
        	values1.put(DATA_BODY_KEY_CUR_ROW, "N");
        	int cnt = db.update(DATA_BODY_TABLE, values1, DATA_BODY_KEY_ID + " =?", new String[] { String.valueOf(currVal)} );
        	
        	ContentValues values = new ContentValues();
    		values.put(DATA_BODY_KEY_CUR_ROW, "C");
        	
    		int cnt1 = db.update(DATA_BODY_TABLE, values, DATA_BODY_KEY_ID + " =?", new String[] { String.valueOf(currVal+1)} );
        	
            Cursor c2 = db.rawQuery(currRow, null);
            if(c2 != null && c2.moveToFirst() ) {
	        	data.setAccountno(c2.getInt(c2.getColumnIndex(DATA_BODY_KEY_ACC_NO)));
	        	data.setCName(c2.getString(c2.getColumnIndex(DATA_BODY_KEY_CUST_NAME)));
	        	data.setCurrentDue(c2.getInt(c2.getColumnIndex(DATA_BODY_KEY_CURR_DUE)));
	        	data.setId(c2.getInt(c2.getColumnIndex(DATA_BODY_KEY_ID)));
	        	data.setMobile(c2.getString(c2.getColumnIndex(DATA_BODY_KEY_MOBILE)));
	        	data.setOpeningDate(c2.getString(c2.getColumnIndex(DATA_BODY_KEY_OPEN_DATE)));
	        	data.setOutstanding(c2.getInt(c2.getColumnIndex(DATA_BODY_KEY_OUT_STAND)));
	        	data.setPaidDue(c2.getInt(c2.getColumnIndex(DATA_BODY_KEY_PAID_DUE)));
	        	if((c2.getString(c2.getColumnIndex(DATA_BODY_KEY_SAVED))).equals("Y")) {
	        		data.setPaidStatus(true);
	        	} else {
	        		data.setPaidStatus(false);
	        	}
	        	data.setEnaDisable(true);	// Enable
            }
            c2.close();
        }
        
    	db.close();
    	return data;
    }
    
    public static BodyData getPrevDataBody() {    	
    	
    	final SQLiteDatabase db = open();
    	int minVal = 0, currVal = 0;
    	
    	String anyCurrRow = "SELECT max(_id) AS maxid FROM " + DATA_BODY_TABLE;
        Cursor c = db.rawQuery(anyCurrRow, null);
        
        if( c != null && c.moveToFirst() ){
	        minVal = c.getInt(0);
        }
        c.close();
        
        String currRow = "SELECT * FROM " + DATA_BODY_TABLE + " WHERE CURR_ROW = \"C\" ";
        Cursor c1 = db.rawQuery(currRow, null);
        
        if( c1 != null && c1.moveToFirst() ){
	        currVal = c1.getInt(c1.getColumnIndex(DATA_BODY_KEY_ID));
        }
        c1.close();
        
        BodyData data = new BodyData();
        
        if(currVal==1) {
        	data.setEnaDisable(false);  // Disable
        } else {
        	
        	ContentValues values1 = new ContentValues();
        	values1.put(DATA_BODY_KEY_CUR_ROW, "N");
        	int cnt = db.update(DATA_BODY_TABLE, values1, DATA_BODY_KEY_ID + " =?", new String[] { String.valueOf(currVal)} );
        	
        	ContentValues values = new ContentValues();
    		values.put(DATA_BODY_KEY_CUR_ROW, "C");
        	
    		int cnt1 = db.update(DATA_BODY_TABLE, values, DATA_BODY_KEY_ID + " =?", new String[] { String.valueOf(currVal-1)} );
        	
            Cursor c2 = db.rawQuery(currRow, null);
            if(c2 != null && c2.moveToFirst() ) {
	        	data.setAccountno(c2.getInt(c2.getColumnIndex(DATA_BODY_KEY_ACC_NO)));
	        	data.setCName(c2.getString(c2.getColumnIndex(DATA_BODY_KEY_CUST_NAME)));
	        	data.setCurrentDue(c2.getInt(c2.getColumnIndex(DATA_BODY_KEY_CURR_DUE)));
	        	data.setId(c2.getInt(c2.getColumnIndex(DATA_BODY_KEY_ID)));
	        	data.setMobile(c2.getString(c2.getColumnIndex(DATA_BODY_KEY_MOBILE)));
	        	data.setOpeningDate(c2.getString(c2.getColumnIndex(DATA_BODY_KEY_OPEN_DATE)));
	        	data.setOutstanding(c2.getInt(c2.getColumnIndex(DATA_BODY_KEY_OUT_STAND)));
	        	data.setPaidDue(c2.getInt(c2.getColumnIndex(DATA_BODY_KEY_PAID_DUE)));
	        	if((c2.getString(c2.getColumnIndex(DATA_BODY_KEY_SAVED))).equals("Y")) {
	        		data.setPaidStatus(true);
	        	} else {
	        		data.setPaidStatus(false);
	        	}
	        	data.setEnaDisable(true);	// Enable
            }
            c2.close();
        }
        
    	db.close();
    	return data;
    }
    
    public static String saveBodyData(int accountno, int paiddue) {
    	final SQLiteDatabase db = open();
    	
    	String ids, saved_flag = "Y", returnString = null; 
    	String validateAccountNo = "SELECT _id, saved, out_standing FROM " + DATA_BODY_TABLE + " where account_no=? ";
    	
    	Cursor uc = db.rawQuery(validateAccountNo, new String[] {String.valueOf(accountno)});
    	
    	if(uc != null && uc.moveToFirst() ) {
    		ids = uc.getString(0);
    		saved_flag = uc.getString(1);
    		
    		if (saved_flag.equals("Y")) {
        		returnString = "Data already saved";
        	} else {
        		ContentValues values = new ContentValues();
        		values.put(DATA_BODY_KEY_PAID_DUE, paiddue);
        		values.put(DATA_BODY_KEY_SAVED, "Y");
        		
        		if(paiddue >= uc.getInt(2)) {
        			values.put(DATA_BODY_KEY_ACCCLOSED, "CLOSE");
        		}
            	
        		int cnt1 = db.update(DATA_BODY_TABLE, values, DATA_BODY_KEY_ID + " =?", new String[] { String.valueOf(ids)} );
            	
        		returnString = "Saved successfully.";
        	}
    		
    	} else {
    		returnString = "No data found";
    	}
    	uc.close();
    	
    	

    	db.close();
		return returnString;
    }
    
    public static String getUsername() {
    	final SQLiteDatabase db = open();
    	
    	String username = null, usernameSQL = "SELECT USERNAME FROM " + DATA_HEADER_TABLE ;
    	Cursor uc = db.rawQuery(usernameSQL, null);
    	
    	if(uc != null && uc.moveToFirst() ) {
    		username = uc.getString(0);
    	
    		if(username.isEmpty() || username == null || username == "") {
        		username = "NILL";
        	}
    		
    	} else {
    		username = "NILL";
    	}
    	
    	uc.close();
    	
    	db.close();
    	return username;
    }
    
    public static String getPassword() {
    	final SQLiteDatabase db = open();
    	
    	String password = null, usernameSQL = "SELECT PASSWORD FROM " + DATA_HEADER_TABLE ;
    	Cursor uc = db.rawQuery(usernameSQL, null);
    	
    	if(uc != null && uc.moveToFirst() ) {
    		password = uc.getString(0);
    	}
    	
    	uc.close();
    	
    	if(password.isEmpty() || password == null || password == "") {
    		password = "NILL";
    	}
    	
    	db.close();
    	return password;
    }
    
    public static boolean isValidAccountNo(int accntno) {
    	final SQLiteDatabase db = open();
    	
    	boolean returnVar;
    	String accountnoSQL = "SELECT _id FROM " + DATA_BODY_TABLE + " where account_no=?";
    	
    	Cursor uc = db.rawQuery(accountnoSQL, new String[] {String.valueOf(accntno)});

    	if(uc != null && uc.moveToFirst() ) {
    		returnVar = true;
    	} else {
    		returnVar = false;
    	}

    	db.close();
    	return returnVar;
    }
    
    public static BodyData getDataBody(int accountNo) {    	
    	
    	final SQLiteDatabase db = open();
    	
    	BodyData data = new BodyData();
    	        	
    	String currRow = "SELECT * FROM " + DATA_BODY_TABLE + " where account_no=? ";
            Cursor c2 = db.rawQuery(currRow, new String[] {String.valueOf(accountNo)});
            if(c2 != null && c2.moveToFirst() ) {
            	
            	String vSQL = "SELECT _id FROM " + DATA_BODY_TABLE + " WHERE CURR_ROW = \"C\" ";
                Cursor c3 = db.rawQuery(vSQL, null);
                
                if(c3 != null && c3.moveToFirst() ) {
                	ContentValues values = new ContentValues();
            		values.put(DATA_BODY_KEY_CUR_ROW, "N");
                	
            		int cnt1 = db.update(DATA_BODY_TABLE, values, DATA_BODY_KEY_ID + " =?", new String[] { String.valueOf(c3.getInt(c3.getColumnIndex(DATA_BODY_KEY_ID)))} );
                }
            	
            	ContentValues values = new ContentValues();
        		values.put(DATA_BODY_KEY_CUR_ROW, "C");
            	
        		int cnt1 = db.update(DATA_BODY_TABLE, values, DATA_BODY_KEY_ID + " =?", new String[] { String.valueOf(c2.getInt(c2.getColumnIndex(DATA_BODY_KEY_ID)))} );
            	
            	data.setAccountno(c2.getInt(c2.getColumnIndex(DATA_BODY_KEY_ACC_NO)));
	        	data.setCName(c2.getString(c2.getColumnIndex(DATA_BODY_KEY_CUST_NAME)));
	        	data.setCurrentDue(c2.getInt(c2.getColumnIndex(DATA_BODY_KEY_CURR_DUE)));
	        	data.setId(c2.getInt(c2.getColumnIndex(DATA_BODY_KEY_ID)));
	        	data.setMobile(c2.getString(c2.getColumnIndex(DATA_BODY_KEY_MOBILE)));
	        	data.setOpeningDate(c2.getString(c2.getColumnIndex(DATA_BODY_KEY_OPEN_DATE)));
	        	data.setOutstanding(c2.getInt(c2.getColumnIndex(DATA_BODY_KEY_OUT_STAND)));
	        	data.setPaidDue(c2.getInt(c2.getColumnIndex(DATA_BODY_KEY_PAID_DUE)));
	        	if((c2.getString(c2.getColumnIndex(DATA_BODY_KEY_SAVED))).equals("Y")) {
	        		data.setPaidStatus(true);
	        	} else {
	        		data.setPaidStatus(false);
	        	}
	        	data.setEnaDisable(true);	// Enable
            }
            c2.close();
        
    	db.close();
    	return data;
    }
    
    public static Cursor getAllDataBody() {
    	final SQLiteDatabase db = open();
    	Cursor c2 = null;
    		String qry = "SELECT * FROM " + DATA_BODY_TABLE + " where saved = 'Y' ";
    		c2 = db.rawQuery(qry, null);
    	//db.close();
    	return c2;
    }
    
    public static String setCollectionStarted(String vehicleno, int startkm) {
    	final SQLiteDatabase db = open();
	    	String currRow = "SELECT * FROM " + DATA_HEADER_TABLE ;
	        Cursor c2 = db.rawQuery(currRow, null);
	        
	        String returnStr = null;
	        
	        if(c2 != null && c2.moveToFirst() ) {
	        	String csVal = c2.getString(c2.getColumnIndex(DATA_HEADER_KEY_COLLSTARTED));
	        	
	        	if(csVal.equals("N")) {
	        		ContentValues values = new ContentValues();
	        		values.put(DATA_HEADER_KEY_VEHICLENO, vehicleno);
	        		values.put(DATA_HEADER_KEY_STARTKM, startkm);
	            	
	        		int cnt1 = db.update(DATA_BODY_TABLE, values, null, null);
	        		returnStr = "Updated Successfully " +cnt1;
	        	}
	        	
	        }
        	
    	db.close();
    	return returnStr;
    }
    
    public static HeaderData getCollectionStatus() {
    	final SQLiteDatabase db = open();
    		
    		String currRow = "SELECT * FROM " + DATA_HEADER_TABLE ;
    		Cursor c2 = db.rawQuery(currRow, null);
    		
    		HeaderData hd = new HeaderData();
    		
    		if(c2 != null && c2.moveToFirst() ) {
    			hd.setUsername(c2.getString(c2.getColumnIndex(DATA_HEADER_KEY_USERNAME)));
    			hd.setUsername1(c2.getString(c2.getColumnIndex(DATA_HEADER_KEY_USERNAME1)));
    			hd.setPassword(c2.getInt(c2.getColumnIndex(DATA_HEADER_KEY_PASSWORD)));
    			hd.setPassword1(c2.getInt(c2.getColumnIndex(DATA_HEADER_KEY_PASSWORD1)));
    			hd.setFinance(c2.getString(c2.getColumnIndex(DATA_HEADER_KEY_FINANCE)));
    			hd.setArea(c2.getString(c2.getColumnIndex(DATA_HEADER_KEY_AREA)));
    			hd.setTemp(c2.getString(c2.getColumnIndex(DATA_HEADER_KEY_TEMPFIELD)));
    			hd.setFilename(c2.getString(c2.getColumnIndex(DATA_HEADER_KEY_FILENAME)));
	        	hd.setCollStarted(c2.getString(c2.getColumnIndex(DATA_HEADER_KEY_COLLSTARTED)));
	        	hd.setVehicleNo(c2.getString(c2.getColumnIndex(DATA_HEADER_KEY_VEHICLENO)));
	        	hd.setStartKm(c2.getInt(c2.getColumnIndex(DATA_HEADER_KEY_STARTKM)));
	        	hd.setEndKm(c2.getInt(c2.getColumnIndex(DATA_HEADER_KEY_ENDKM)));
	        	hd.setOtherExpenses(c2.getInt(c2.getColumnIndex(DATA_HEADER_KEY_OTHEREXPENSES)));
    		} else {
    			hd.setCollStarted("C");
    		}
    		
    	db.close();
    	return hd;
    }
    
    public static boolean saveCollectionHeader(String vehicleno, int startkm, int endkm, String collstatus, int otherexpenses) {
    	final SQLiteDatabase db = open();
    		boolean returnval = false;
    		
    		ContentValues values = new ContentValues();
    		values.put(DATA_HEADER_KEY_COLLSTARTED, collstatus);
    		values.put(DATA_HEADER_KEY_VEHICLENO, vehicleno);
    		values.put(DATA_HEADER_KEY_STARTKM, startkm);
    		values.put(DATA_HEADER_KEY_ENDKM, endkm);
    		values.put(DATA_HEADER_KEY_OTHEREXPENSES, otherexpenses);
        	
    		int cnt1 = db.update(DATA_HEADER_TABLE, values, null, null);
    		
    		if(cnt1>0){
    			returnval =true;
    		}
    		
    	db.close();
    	return returnval;
    }
    
    
}
