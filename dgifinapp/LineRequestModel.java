package com.infoforarun.arun.dgifinapp;

/**
 * Created by Arun on 1/26/2017.
 */

public class LineRequestModel {
    private String _linename, _lineno, _linestatus;

    //Empty constructor
    public LineRequestModel() {

    }

    public LineRequestModel(String linename, String lineno, String linestatus) {
        this._linename 	 = linename;
        this._lineno 	 = lineno;
        this._linestatus = linestatus;
    }

    public void setLineName(String linename) {
        this._linename 	= linename;
    }

    public void setLineNo(String lineno) { this._lineno	= lineno; }

    public void setLineStatus(String linestatus) {
        this._linestatus = linestatus;
    }

    public String getLineName() {
        return this._linename;
    }

    public String getLineNo() {
        return this._lineno;
    }

    public String getLineStatus() {
        return this._linestatus;
    }

}
