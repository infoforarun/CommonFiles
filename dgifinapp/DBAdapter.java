package com.infoforarun.arun.dgifinapp;

/**
 * Created by Arun on 1/31/2017.
 */
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

import static android.R.attr.data;

public class DBAdapter {
    /******** if debug is set true then it will show all Logcat message *******/
    public static final boolean DEBUG = true;

    /******************** Logcat TAG ************/
    public static final String LOG_TAG = "DBAdapter";

    /******************** Table Fields ************/
    public static final String COLLECTION_KEY_ID 			= "_id";
    public static final String COLLECTION_KEY_ACC_NO		= "account_no";
    public static final String COLLECTION_KEY_ACC_O_DT		= "account_o_dt";
    public static final String COLLECTION_KEY_BALANCE_AMT	= "balance_amt";
    public static final String COLLECTION_KEY_CUST_NAME		= "customer_name";
    public static final String COLLECTION_KEY_MOBILE		= "mobile";
    public static final String COLLECTION_KEY_DUE_AMT		= "due_amt";
    public static final String COLLECTION_KEY_PAID_AMT		= "paid_amt";
    public static final String COLLECTION_KEY_CURR_ROW		= "curr_row";
    public static final String COLLECTION_KEY_SAVED			= "saved";
    public static final String COLLECTION_KEY_SAVED_ON		= "saved_on";
    public static final String COLLECTION_KEY_SYNCED		= "synced";
    public static final String COLLECTION_KEY_SYNCED_ON		= "synced_on";


    public static final String METADATA_KEY_ID				= "_id";
    public static final String METADATA_KEY_USERNAME		= "username";
    public static final String METADATA_KEY_PASSWORD		= "password";
    public static final String METADATA_KEY_FINANCE_CD		= "finance_cd";
    public static final String METADATA_KEY_CUSTOMER_ID		= "customer_id";
    public static final String METADATA_KEY_COLLSTARTED		= "coll_started";
    public static final String METADATA_KEY_OFFLINE_COLL	= "offline_coll";
    public static final String METADATA_KEY_APP_VERSION		= "app_version";

    /******************** Database Name ************/
    public static final String DATABASE_NAME = "DB_DgiFin_App";

    /**** Database Version (Increase one if want to also upgrade your database) ***/
    public static final int DATABASE_VERSION = 1; // started at 1

    /******************** Table names   ************/
    public static final String COLLECTION_TABLE = "collection_t";
    public static final String METADATA_TABLE = "metadata_t";

    /******* Set all table with comma separated like USER_TABLE,ABC_TABLE *******/
    private static final String[ ] ALL_TABLES = {COLLECTION_TABLE,  METADATA_TABLE};

    private static final String COLLECTION_TABLE_CREATE =   "create table "+ COLLECTION_TABLE + " ( " +
            COLLECTION_KEY_ID 			+ " integer primary key autoincrement, " +
            COLLECTION_KEY_ACC_NO		+ " text, " +
            COLLECTION_KEY_ACC_O_DT		+ " text, "+
            COLLECTION_KEY_BALANCE_AMT	+ " integer, " +
            COLLECTION_KEY_CUST_NAME	+ " text, " +
            COLLECTION_KEY_MOBILE		+ " text, " +
            COLLECTION_KEY_DUE_AMT		+ " integer, " +
            COLLECTION_KEY_PAID_AMT		+ " integer, " +
            COLLECTION_KEY_CURR_ROW		+ " text, " +
            COLLECTION_KEY_SAVED		+ " text, " +
            COLLECTION_KEY_SAVED_ON		+ " text, " +
            COLLECTION_KEY_SYNCED		+ " text, " +
            COLLECTION_KEY_SYNCED_ON	+ " text " + "); ";

    private static final String METADATA_TABLE_CREATE = 	"create table "+ METADATA_TABLE + " ( " +
            METADATA_KEY_ID				+ " integer primary key autoincrement, " +
            METADATA_KEY_USERNAME		+ " text, " +
            METADATA_KEY_PASSWORD		+ " text, " +
            METADATA_KEY_FINANCE_CD		+ " text, " +
            METADATA_KEY_CUSTOMER_ID	+ " text, " +
            METADATA_KEY_COLLSTARTED	+ " text, " +
            METADATA_KEY_OFFLINE_COLL	+ " text, " +
            METADATA_KEY_APP_VERSION	+ " text " + "); ";

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
                db.execSQL(COLLECTION_TABLE_CREATE);
                db.execSQL(METADATA_TABLE_CREATE);

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
    }

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

    public static void insertCollection(CollectionModel cData) {

        // Open database for Read / Write
        final SQLiteDatabase db = open();

        String vAccountNo 	= sqlEscapeString(cData.getAccountNo());
        String vAccountODt	= sqlEscapeString(cData.getAcntOpenDate());
        String vCustomerNm	= sqlEscapeString(cData.getCustomerName());
        String vMobileNo	= sqlEscapeString(cData.getMobile());
        //String Vsavedon = new SimpleDateFormat("yyMMddHHmmss").format(Calendar.getInstance().getTime());

        int vDueAmt			= cData.getDueAmt();
        int vPaidAmt		= cData.getPaidAmt();
        int vBalanceAmt		= cData.getBalanceAmt();

        ContentValues cVal = new ContentValues();

        cVal.put(COLLECTION_KEY_ACC_NO, 	vAccountNo);
        cVal.put(COLLECTION_KEY_ACC_O_DT, 	vAccountODt);
        cVal.put(COLLECTION_KEY_BALANCE_AMT,vBalanceAmt);
        cVal.put(COLLECTION_KEY_CUST_NAME, 	vCustomerNm);
        cVal.put(COLLECTION_KEY_MOBILE, 	vMobileNo);
        cVal.put(COLLECTION_KEY_DUE_AMT, 	vDueAmt);
        cVal.put(COLLECTION_KEY_PAID_AMT, 	vPaidAmt);
        cVal.put(COLLECTION_KEY_CURR_ROW, 	"N");
        cVal.put(COLLECTION_KEY_SAVED, 		"N");
        cVal.put(COLLECTION_KEY_SAVED_ON, 	"X");   // A temp replacement for DateTime
        cVal.put(COLLECTION_KEY_SYNCED, 	"N");
        cVal.put(COLLECTION_KEY_SYNCED_ON, 	"X");   // A temp replacement for DateTime

        db.insert(COLLECTION_TABLE, null, cVal);

        db.close();
    }

    public static void insertMetadata(MetadataModel mData) {

        final SQLiteDatabase db = open();

        String vUsername	= sqlEscapeString(mData.getUserName());
        String vPassword	= sqlEscapeString(mData.getPassword());
        String vFinanceCd	= sqlEscapeString(mData.getFinancecd());
        String vCustomerID	= sqlEscapeString(mData.getCusotmerId());

        ContentValues cVal = new ContentValues();

        cVal.put(METADATA_KEY_USERNAME, 	vUsername);
        cVal.put(METADATA_KEY_PASSWORD, 	vPassword);
        cVal.put(METADATA_KEY_FINANCE_CD,	vFinanceCd);
        cVal.put(METADATA_KEY_CUSTOMER_ID, 	vCustomerID);
        cVal.put(METADATA_KEY_COLLSTARTED, 	"N");
        cVal.put(METADATA_KEY_OFFLINE_COLL, "Y");
        cVal.put(METADATA_KEY_APP_VERSION, 	"Beta 1.0");

        db.insert(METADATA_TABLE, null, cVal);
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

    public static void setCollectionStarted(boolean collStarted) {

        final SQLiteDatabase db = open();
        String sql = "SELECT * FROM " + METADATA_TABLE ;
        Cursor c2 = db.rawQuery(sql, null);

        if(c2 != null && c2.moveToFirst() ) {
            ContentValues values = new ContentValues();
            if(collStarted) {
                values.put(METADATA_KEY_COLLSTARTED, "Y");
            } else {
                values.put(METADATA_KEY_COLLSTARTED, "N");
            }
            db.update(METADATA_TABLE, values, null, null);
        }

        db.close();
    }

    public static boolean getCollectionStarted() {
        final SQLiteDatabase db = open();

        boolean collstarted;
        String qry 	= "select " + METADATA_KEY_COLLSTARTED +
                " from " + METADATA_TABLE;
        Cursor uc = db.rawQuery(qry, null);
        if(uc != null && uc.moveToFirst()) {
            if(uc.getString(0).equals("Y"))
                collstarted = true;
            else
                collstarted = false;
        }
        else
            collstarted = false;

        db.close();
        return collstarted;
    }

    public static String saveCollection(int accountno, int paiddue) {
        final SQLiteDatabase db = open();
        String mReturnString, mId, msavedFlag;

        String query = "select " +  COLLECTION_KEY_ID +
                " , " +      COLLECTION_KEY_SAVED +
                " from " +   COLLECTION_TABLE +
                " where " +  COLLECTION_KEY_ACC_NO +
                "=? ";

        Cursor crsr = db.rawQuery(query, new String[] {String.valueOf(accountno)});

        if(crsr != null && crsr.moveToFirst() ) {
            mId         = crsr.getString(0);
            msavedFlag  = crsr.getString(1);

            if(msavedFlag.equals("Y")) {
                mReturnString = "DATA SAVED ALREADY";
            } else {
                ContentValues cv = new ContentValues();
                cv.put(COLLECTION_KEY_PAID_AMT, paiddue);
                cv.put(COLLECTION_KEY_SAVED, "Y");

                int cnt1 = db.update(COLLECTION_TABLE, cv, COLLECTION_KEY_ID + " =?", new String[] { String.valueOf(mId)} );
                return "Saved Successfully.";
            }
        } else
            mReturnString = "Account No: " + accountno + " not found";

        return mReturnString;
    }

    public static CollectionModel getNextPrevData(String np) {   //Next or Previous

        final SQLiteDatabase db = open();
        int maxVal = 0, currVal = 0, updateId = 0;

        String sql =    "select max( " + COLLECTION_KEY_ID + " ) as maxid " +
                        " from " + COLLECTION_TABLE;

        Cursor c = db.rawQuery(sql, null);
        if( c != null && c.moveToFirst() ){
            maxVal = c.getInt(0);
        }
        c.close();

        String currRow =   "select * from " + COLLECTION_TABLE +
                            " where " + COLLECTION_KEY_CURR_ROW +
                            "= \"C\" ";
        Cursor c1 = db.rawQuery(currRow, null);
        if( c1 != null && c1.moveToFirst() ){
            currVal = c1.getInt(c1.getColumnIndex(COLLECTION_KEY_ID));
        } else
            currVal = 0;
        c1.close();

        CollectionModel cm = new CollectionModel();

        if(currVal>=0 && currVal<=maxVal) {
            ContentValues values1 = new ContentValues();

            if(currVal>=0) {
                values1.put(COLLECTION_KEY_CURR_ROW, "N");
                int cnt = db.update(COLLECTION_TABLE, values1, COLLECTION_KEY_ID + " =?", new String[]{String.valueOf(currVal)});
            }
            ContentValues values = new ContentValues();
            values.put(COLLECTION_KEY_CURR_ROW, "C");

            if(np.equals("N"))
                updateId = currVal+1;
            else if(np.equals("P"))
                updateId = currVal-1;
            else if(np.equals("C")) {
                if(currVal==0)
                    updateId = currVal+1;
                else
                    updateId = currVal;
            }

            int cnt1 = db.update(COLLECTION_TABLE, values, COLLECTION_KEY_ID + " =?", new String[] {String.valueOf(updateId)} );

            Cursor c2 = db.rawQuery(currRow, null);
            if(c2 != null && c2.moveToFirst() ) {
                cm.setBalanceAmt(c2.getInt(c2.getColumnIndex(COLLECTION_KEY_BALANCE_AMT)));
                cm.setDueAmt(c2.getInt(c2.getColumnIndex(COLLECTION_KEY_DUE_AMT)));
                cm.setPaidAmt(c2.getInt(c2.getColumnIndex(COLLECTION_KEY_PAID_AMT)));
                cm.setAccountNo(c2.getString(c2.getColumnIndex(COLLECTION_KEY_ACC_NO)));
                cm.setAcntOpenDate(c2.getString(c2.getColumnIndex(COLLECTION_KEY_ACC_O_DT)));
                cm.setCustomerName(c2.getString(c2.getColumnIndex(COLLECTION_KEY_CUST_NAME)));
                cm.setMobile(c2.getString(c2.getColumnIndex(COLLECTION_KEY_MOBILE)));
                String mSaved = c2.getString(c2.getColumnIndex(COLLECTION_KEY_SAVED));
                if(mSaved.equals("Y"))
                    cm.setSaved(true);
                else
                    cm.setSaved(false);
                String mPn;
                if(updateId==1)
                    mPn = "N";
                else if (maxVal==updateId)
                    mPn = "P";
                else
                    mPn = "NP";
                cm.setPrevNext(mPn);
            }
            c2.close();
        }

        db.close();
        return cm;
    }

    public static void setCurrRowReset() {

        final SQLiteDatabase db = open();

        ContentValues values = new ContentValues();
        values.put(COLLECTION_KEY_CURR_ROW, "C");

        int cnt1 = db.update(COLLECTION_TABLE, values, COLLECTION_KEY_ID + " =?", new String[] {String.valueOf(1)} );
        db.close();
    }

    public static int getUserDataCount() {

        final SQLiteDatabase db = open();

        String cQuery = "select count(*) as cnt from "+ COLLECTION_TABLE;
        Cursor cursor = db.rawQuery(cQuery, null);
        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        // return count
        return count;
    }

    public static String getUsername() {

        final SQLiteDatabase db = open();

        String  username 	= null,
                usernameSql = "select " + METADATA_KEY_USERNAME +
                        " from " + METADATA_TABLE ;
        Cursor uC = db.rawQuery(usernameSql, null);

        if(uC != null && uC.moveToFirst() ) {
            username = uC.getString(0);

            if(username.isEmpty() && username == null && username == "") {
                username = "NILL";
            }
        } else {
            username = "NILL";
        }

        db.close();
        return username;
    }


    public static String getPassword() {

        final SQLiteDatabase db = open();

        String password = null, usernameSQL = "select " + METADATA_KEY_PASSWORD +
                " from " + METADATA_TABLE ;
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

    public static boolean isValidAccountNo(String accntno) {

        final SQLiteDatabase db = open();

        boolean returnVar;

        String accountnoSQL = "select " + COLLECTION_KEY_ID +
                " from " + COLLECTION_TABLE +
                " where " + COLLECTION_KEY_ACC_NO +
                " =?";

        Cursor uc = db.rawQuery(accountnoSQL, new String[] {String.valueOf(accntno)});

        if(uc != null && uc.moveToFirst() ) {
            returnVar = true;
        } else {
            returnVar = false;
        }

        db.close();
        return returnVar;
    }

    public static boolean offlineLoginEnabled() {
        final SQLiteDatabase db = open();
        boolean returnVal;

        String offlineLoginSql = "select "+ METADATA_KEY_OFFLINE_COLL +
                " from " + METADATA_TABLE;

        Cursor uc = db.rawQuery(offlineLoginSql, null);
        if(uc != null && uc.moveToFirst() ) {
            String oL= uc.getString(0);
            if(oL.equals("Y"))
                returnVal = true;
            else
                returnVal = false;
        } else
            returnVal = false;
        db.close();
        return returnVal;
    }

    public static Cursor getAllCollection() {
        final SQLiteDatabase db = open();

        Cursor c 	= null;
        String qry 	= "select * from " +
                COLLECTION_TABLE +
                " where " + COLLECTION_KEY_SAVED +
                " = 'Y' ";

        c = db.rawQuery(qry, null);
        //db.close();
        return c;
    }

    public static String getCustomerId() {
        final SQLiteDatabase db = open();

        String customerId;
        String qry 	= "select " + METADATA_KEY_CUSTOMER_ID +
                " from " + METADATA_TABLE;
        Cursor uc = db.rawQuery(qry, null);
        if(uc != null && uc.moveToFirst())
            customerId = uc.getString(0);
        else
            customerId = "INVALID";

        db.close();
        return customerId;
    }

    public static CollectionModel getCollectionForAccNo(String accountNo) {

        final SQLiteDatabase db = open();
        int maxVal = 0, currVal = 0, updateId = 0;
        CollectionModel cm = new CollectionModel();

        String sql =    "select max( " + COLLECTION_KEY_ID + " ) as maxid " +
                " from " + COLLECTION_TABLE;

        Cursor c = db.rawQuery(sql, null);
        if( c != null && c.moveToFirst() ){
            maxVal = c.getInt(0);
        }
        c.close();

        String currRow =   "select * from " + COLLECTION_TABLE +
                " where " + COLLECTION_KEY_CURR_ROW +
                "= \"C\" ";
        Cursor c1 = db.rawQuery(currRow, null);
        if( c1 != null && c1.moveToFirst() ){
            currVal = c1.getInt(c1.getColumnIndex(COLLECTION_KEY_ID));

            ContentValues values1 = new ContentValues();
            values1.put(COLLECTION_KEY_CURR_ROW, "N");
            int cnt = db.update(COLLECTION_TABLE, values1, COLLECTION_KEY_ID + " =?", new String[]{String.valueOf(currVal)});
        }
        c1.close();



        String moveToRow =   "select * from " + COLLECTION_TABLE +
                " where " +  COLLECTION_KEY_ACC_NO +
                "=? ";
        Cursor c2 = db.rawQuery(moveToRow, new String[] {accountNo});

        if(c2 != null && c2.moveToFirst() ) {
            updateId = c2.getInt(c2.getColumnIndex(COLLECTION_KEY_ID));

            cm.setBalanceAmt(c2.getInt(c2.getColumnIndex(COLLECTION_KEY_BALANCE_AMT)));
            cm.setDueAmt(c2.getInt(c2.getColumnIndex(COLLECTION_KEY_DUE_AMT)));
            cm.setPaidAmt(c2.getInt(c2.getColumnIndex(COLLECTION_KEY_PAID_AMT)));
            cm.setAccountNo(c2.getString(c2.getColumnIndex(COLLECTION_KEY_ACC_NO)));
            cm.setAcntOpenDate(c2.getString(c2.getColumnIndex(COLLECTION_KEY_ACC_O_DT)));
            cm.setCustomerName(c2.getString(c2.getColumnIndex(COLLECTION_KEY_CUST_NAME)));
            cm.setMobile(c2.getString(c2.getColumnIndex(COLLECTION_KEY_MOBILE)));
            String mSaved = c2.getString(c2.getColumnIndex(COLLECTION_KEY_SAVED));
            if(mSaved.equals("Y"))
                cm.setSaved(true);
            else
                cm.setSaved(false);
            String mPn;
            if(updateId==1)
                mPn = "N";
            else if (maxVal==updateId)
                mPn = "P";
            else
                mPn = "NP";
            cm.setPrevNext(mPn);

            ContentValues values = new ContentValues();
            values.put(COLLECTION_KEY_CURR_ROW, "C");
            int cnt1 = db.update(COLLECTION_TABLE, values, COLLECTION_KEY_ID + " =?", new String[] {String.valueOf(updateId)} );
        }
        c2.close();

        db.close();
        return cm;
    }
}