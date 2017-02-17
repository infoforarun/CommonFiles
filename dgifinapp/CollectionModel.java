package com.infoforarun.arun.dgifinapp;

/**
 * Created by Arun on 2/1/2017.
 */

public class CollectionModel {
    private String 	_accountno, _acntopndate, _custname, _mobile, _savedon, _syncedon, _pn;
    private int 	_balanceamt, _dueamt, _paidamt;
    private boolean _currrow, _saved, _synced;

    //Empty Constructor
    public CollectionModel() {

    }

    public CollectionModel(int balanceamt, int dueamt, int paidamt, String accountno, String acntopndate, String custname, String mobile, boolean saved, boolean synced, String pn) {
        this._balanceamt = balanceamt;
        this._dueamt = dueamt;
        this._paidamt = paidamt;
        this._accountno = accountno;
        this._acntopndate = acntopndate;
        this._custname = custname;
        this._mobile = mobile;
        this._saved = saved;
        this._synced = synced;
    }

    //Set
    public void setBalanceAmt(int balanceamt) { this._balanceamt = balanceamt; }

    public void setDueAmt(int dueamt) { this._dueamt = dueamt; }

    public void setPaidAmt(int paidamt) { this._paidamt = paidamt; }

    public void setAccountNo(String accountno) { this._accountno = accountno; }

    public void setAcntOpenDate(String acntopndate) { this._acntopndate = acntopndate; }

    public void setCustomerName(String custname) { this._custname = custname; }

    public void setMobile(String mobile) { this._mobile = mobile; }

    public void setSaved(boolean saved) { this._saved = saved; }

    public void setSynced(boolean synced) { this._synced = synced; }

    public void setPrevNext(String pn) { this._pn = pn; }

    //Get

    public int getBalanceAmt() { return this._balanceamt; }

    public int getDueAmt() { return this._dueamt; }

    public int getPaidAmt() { return this._paidamt; }

    public String getAccountNo() { return this._accountno; }

    public String getAcntOpenDate() { return this._acntopndate; }

    public String getCustomerName() { return this._custname; }

    public String getMobile() { return this._mobile; }

    public boolean getSaved() { return this._saved; }

    public boolean getSynced() { return this._synced; }

    public String getPrevNext() { return this._pn; }

}