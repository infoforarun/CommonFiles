package com.infoforarun.arun.dgifinapp;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
 
public class CollectionReportAdapter extends BaseAdapter {
 
 Context context;
 ArrayList<CollectionModel> bdlist;
 
 public CollectionReportAdapter(Context context, ArrayList<CollectionModel> list) {
 
  this.context = context;
  bdlist = list;
 }
 
 @Override
 public int getCount() {
 
  return bdlist.size();
 }
 
 @Override
 public Object getItem(int position) {
 
  return bdlist.get(position);
 }
 
 @Override
 public long getItemId(int position) {
 
  return position;
 }
 
 @Override
 public View getView(int position, View convertView, ViewGroup arg2) {
  CollectionModel bdListItems = bdlist.get(position);
 
  if (convertView == null) {
   LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
   convertView = inflater.inflate(R.layout.list_item_collection_report, null);
 
  }

  TextView tvSync = (TextView) convertView.findViewById(R.id.lirTvSynced);
  if(bdListItems.getSynced()) {
   tvSync.setText(String.valueOf("\u2713"));
   tvSync.setTextColor(Color.GREEN);
  }
  else {
   tvSync.setText("X");
   tvSync.setTextColor(Color.RED);
  }
  TextView tvAccnNo = (TextView) convertView.findViewById(R.id.lirTvAccounNo);
  tvAccnNo.setText(String.valueOf(bdListItems.getAccountNo()));
  TextView tvName = (TextView) convertView.findViewById(R.id.lirTvName);
  tvName.setText(bdListItems.getCustomerName());
  TextView tvPaidDue = (TextView) convertView.findViewById(R.id.lirTvPaidDue);
  tvPaidDue.setText(String.valueOf(bdListItems.getPaidAmt()));
 
  return convertView;
 }
 
}