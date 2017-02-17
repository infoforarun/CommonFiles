package com.infoforarun.arun.dgifinapp;

/**
 * Created by Arun on 2/1/2017.
 */

public class MetadataModel {

    private String _username, _password, _financecd, _customerid, _appversion;
    boolean _collstarted, _offlinecoll;

    //Empty Constructor
    public 	MetadataModel() {

    }

    public MetadataModel(String username, String password, String financecd, String customerid, boolean collstarted, boolean offlinecoll, String appversion) {
        this._username = username;
        this._password = password;
        this._financecd = financecd;
        this._customerid = customerid;
        this._collstarted = collstarted;
        this._offlinecoll = offlinecoll;
        this._appversion = appversion;
    }

    //Set
    public void setUserName(String username) { this._username = username; }
    public void setPassword(String password) { this._password = password; }
    public void setFinancecd(String financecd) { this._financecd = financecd; }
    public void setCusotmerId(String customerid) { this._customerid = customerid; }
    public void setCollStarted(boolean collstarted) { this._collstarted = collstarted; }
    public void setOfflineColl(boolean offlinecoll) { this._offlinecoll = offlinecoll; }
    public void setAppVersion(String appversion) { this._appversion = appversion; }

    //Get
    public String getUserName() { return this._username; }
    public String getPassword() { return this._password; }
    public String getFinancecd() { return this._financecd; }
    public String getCusotmerId() { return this._customerid; }
    public boolean getCollStarted() { return this._collstarted; }
    public boolean getOfflineColl() { return this._offlinecoll; }
    public String getAppVersion() { return this._appversion; }

}