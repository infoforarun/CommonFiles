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
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Arun on 1/26/2017.
 */

public class LineRequestAdapter extends BaseAdapter{
    /*********** Declare Used Variables *********/
    private Activity activity;
    private ArrayList data;
    private static LayoutInflater inflater=null;
    public Resources res;
    LineRequestModel tempValues=null;
    int i=0;

    /*************  LineRequestAdapter Constructor *****************/
    public LineRequestAdapter(Activity a, ArrayList d,Resources resLocal) {
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
        public TextView linename_tv_vh;
        public TextView lineno_tv_vh;
        public TextView linestatus_tv_vh;
    }

    /****** Depends upon data size called for each row , Create each ListView row *****/
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if(convertView==null){

            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.list_item_line_request, null);

            /****** View Holder Object to contain tabitem.xml file elements ******/

            holder = new ViewHolder();
            holder.linename_tv_vh = (TextView) vi.findViewById(R.id.lri_linename_tv);
            holder.lineno_tv_vh=(TextView)vi.findViewById(R.id.lri_lineno_tv);
            holder.linestatus_tv_vh=(TextView)vi.findViewById(R.id.lri_linestatus_tv);


            /************  Set holder with LayoutInflater ************/
            vi.setTag( holder );
        }
        else
            holder=(ViewHolder)vi.getTag();

        if(data.size()<=0)
        {
            holder.linename_tv_vh.setText("No Data");
        }
        else
        {
            /***** Get each Model object from Arraylist ********/
            tempValues=null;
            tempValues = ( LineRequestModel ) data.get( position );

            /************  Set Model values in Holder elements ***********/

            holder.linename_tv_vh.setText( tempValues.getLineName() );
            holder.lineno_tv_vh.setText( tempValues.getLineNo() );

            String mLineStatus = tempValues.getLineStatus();

            if(mLineStatus.equals("1"))
                mLineStatus = "Approved";
            else if(mLineStatus.equals("2"))
                mLineStatus = "Available for request";
            else if(mLineStatus.equals("3"))
                mLineStatus = "Request in Progress";
            else
                mLineStatus = " ";

            holder.linestatus_tv_vh.setText(mLineStatus);

            /******** Set Item Click Listner for LayoutInflater for each row *******/

            vi.setOnClickListener(new OnItemClickListener( position ));
        }


        return vi;
    }

    public void onClick(View v) {
        Log.v("CustomAdapter", "=====Row button clicked=====");
    }

    /********* Called when Item click in ListView ************/
    private class OnItemClickListener implements OnClickListener{
        private int mPosition;

        OnItemClickListener(int position){
            mPosition = position;
        }

        @Override
        public void onClick(View arg0) {
            LineRequestActivity sct = (LineRequestActivity)activity;
            /****  Call  onItemClick Method inside CustomListViewAndroidExample Class ( See Below )****/
            sct.onItemClick(mPosition);
        }
    }
}
