package com.infoforarun.arun.dgifinapp;

/**
 * Created by Arun on 2/12/2017.
 */

public class AccountHistoryModel {
    private String _date;
    private int _paidamt, _sno;

    public AccountHistoryModel() {

    }

    public AccountHistoryModel(int sno, String date, int paidamt) {
        this._date = date;
        this._paidamt =  paidamt;
        this._sno = sno;
    }

    public void setDate(String date) {
        this._date = date;
    }

    public void setPaidAmt(int paidamt) {
        this._paidamt = paidamt;
    }

    public void setSno(int sno) {
        this._sno = sno;
    }

    public String getDate() {
        return this._date;
    }

    public int getPainAmt() {
        return this._paidamt;
    }

    public int getSno() {
        return this._sno;
    }
}