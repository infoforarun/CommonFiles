package com.infoforarun.arun.dgifinapp;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Arun on 2/6/2017.
 */

public class ManageRequestAdapter extends BaseAdapter {
    /*********** Declare Used Variables *********/
    private Activity activity;
    private ArrayList data;
    private static LayoutInflater inflater=null;
    public Resources res;
    ManageRequestModel tempValues=null;
    int i=0;

    /*************  LineRequestAdapter Constructor *****************/
    public ManageRequestAdapter(Activity a, ArrayList d,Resources resLocal) {
        /********** Take passed values **********/
        activity = a;
        data = d;
        res = resLocal;

        /***********  Layout inflator to call external xml layout () ***********/
        inflater = ( LayoutInflater )activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {

        if(data.size()<=0)
            return 1;
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    /********* Create a holder Class to contain inflated xml file elements *********/
    public static class ViewHolder{
        public TextView requestor_tv_vh;
        public TextView requestdate_tv_vh;
        public TextView lineno_tv_vh;
        public TextView category_tv_vh;
    }

    /****** Depends upon data size called for each row , Create each ListView row *****/
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ManageRequestAdapter.ViewHolder holder;

        if(convertView==null){

            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.list_item_manage_request, null);

            /****** View Holder Object to contain tabitem.xml file elements ******/

            holder = new ManageRequestAdapter.ViewHolder();
            holder.requestor_tv_vh   =(TextView)vi.findViewById(R.id.lim_requestor_tv);
            holder.requestdate_tv_vh =(TextView)vi.findViewById(R.id.lim_request_dt_tv);
            holder.lineno_tv_vh      =(TextView)vi.findViewById(R.id.lim_lineno_tv);
            holder.category_tv_vh    =(TextView)vi.findViewById(R.id.lim_category_tv);


            /************  Set holder with LayoutInflater ************/
            vi.setTag( holder );
        }
        else
            holder=(ManageRequestAdapter.ViewHolder)vi.getTag();

        if(data.size()<=0)
        {
            holder.requestor_tv_vh.setText("No Data");
        }
        else
        {
            /***** Get each Model object from Arraylist ********/
            tempValues=null;
            tempValues = ( ManageRequestModel ) data.get( position );

            /************  Set Model values in Holder elements ***********/

            holder.requestor_tv_vh.setText( tempValues.getCustId() );
            holder.requestdate_tv_vh.setText( tempValues.getRequestDate() );
            holder.lineno_tv_vh.setText( tempValues.getLineNo() );
            holder.category_tv_vh.setText( tempValues.getCategory() );

            /******** Set Item Click Listner for LayoutInflater for each row *******/

            vi.setOnClickListener(new ManageRequestAdapter.OnItemClickListener( position ));
        }

        return vi;
    }

    public void onClick(View v) {
        Log.v("CustomAdapter", "=====Row button clicked=====");
    }

    /********* Called when Item click in ListView ************/
    private class OnItemClickListener implements OnClickListener {
        private int mPosition;

        OnItemClickListener(int position){
            mPosition = position;
        }

        @Override
        public void onClick(View arg0) {
            ManageRequestActivity sct = (ManageRequestActivity)activity;
            /****  Call  onItemClick Method inside CustomListViewAndroidExample Class ( See Below )****/
            sct.onItemClick(mPosition);
        }
    }

}
