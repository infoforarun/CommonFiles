package com.likecorp.app37;

import java.util.ArrayList;

import com.example.app37.R;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ListView;
import android.widget.TextView;

public class ReportActivity extends Activity {
	
	ListView listview = null;
	TextView cntTv, sumTv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		listview = (ListView)findViewById(R.id.list);
		cntTv	 = (TextView)findViewById(R.id.arTvCount);
		sumTv	 = (TextView)findViewById(R.id.arTvDueSum);
		
		DBAdapter.init(this);
		showList();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.report, menu);
		return true;
	}
	
	
	public void showList() {
		ArrayList<BodyData> bdlist = new ArrayList<BodyData>();
		bdlist.clear();
		int paidCnt = 0, paidSum = 0;
		
		Cursor c1 = DBAdapter.getAllDataBody();
		if(c1 != null && c1.moveToFirst() ) {
			if(c1.moveToFirst()) {
				do{
					BodyData bdlistitems = new BodyData();
					
					bdlistitems.setAccountno(c1.getInt(c1.getColumnIndex("account_no")));
					bdlistitems.setCName(c1.getString(c1.getColumnIndex("customer_name")));
					bdlistitems.setPaidDue(c1.getInt(c1.getColumnIndex("paid_due")));
					
					bdlist.add(bdlistitems);
					
					paidCnt++;
					paidSum = paidSum + c1.getInt(c1.getColumnIndex("paid_due"));
					
				} while(c1.moveToNext());
			}
		}
		c1.close();
		
		cntTv.setText(String.valueOf(paidCnt));
		sumTv.setText(String.valueOf(paidSum));
		
		DataBodyListAdapter dbListAdapter = new DataBodyListAdapter(ReportActivity.this, bdlist);
		listview.setAdapter(dbListAdapter);
		
	}

}
