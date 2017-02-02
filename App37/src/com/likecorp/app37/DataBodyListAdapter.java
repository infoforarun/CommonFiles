package com.likecorp.app37;

import java.util.ArrayList;

import com.example.app37.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
 
public class DataBodyListAdapter extends BaseAdapter {
 
 Context context;
 ArrayList<BodyData> bdlist;
 
 public DataBodyListAdapter(Context context, ArrayList<BodyData> list) {
 
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
  BodyData bdListItems = bdlist.get(position);
 
  if (convertView == null) {
   LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
   convertView = inflater.inflate(R.layout.listitem_row, null);
 
  }
  TextView tvAccnNo = (TextView) convertView.findViewById(R.id.lirTvAccounNo);
  tvAccnNo.setText(String.valueOf(bdListItems.getAccountno()));
  TextView tvName = (TextView) convertView.findViewById(R.id.lirTvName);
  tvName.setText(bdListItems.getCName());
  TextView tvPaidDue = (TextView) convertView.findViewById(R.id.lirTvPaidDue);
  tvPaidDue.setText(String.valueOf(bdListItems.getPaidDue()));
 
  return convertView;
 }
 
}