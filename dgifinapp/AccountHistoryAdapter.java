package com.infoforarun.arun.dgifinapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AccountHistoryAdapter extends BaseAdapter {

 Context context;
 ArrayList<AccountHistoryModel> bdlist;

 public AccountHistoryAdapter(Context context, ArrayList<AccountHistoryModel> list) {
 
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
  AccountHistoryModel bdListItems = bdlist.get(position);
 
  if (convertView == null) {
   LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
   convertView = inflater.inflate(R.layout.list_item_collection_history, null);
 
  }
  TextView tvNo = (TextView) convertView.findViewById(R.id.lichTvNo);
  tvNo.setText(String.valueOf(bdListItems.getSno()));
  TextView tvDate = (TextView) convertView.findViewById(R.id.lichTvDate);
  tvDate.setText(bdListItems.getDate());
  TextView tvPaidDue = (TextView) convertView.findViewById(R.id.lichTvPaidDue);
  tvPaidDue.setText(String.valueOf(bdListItems.getPainAmt()));
 
  return convertView;
 }
 
}