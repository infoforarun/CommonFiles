package com.infoforarun.arun.dgifinapp;

import java.io.Serializable;

/**
 * Created by Arun on 2/6/2017.
 */

public class ManageRequestModel implements Serializable {
    private String 	_approvedcustdate, _approvecustid, _category, _custid, _description, _lineno, _maintask, _primarykey, _purpose, _requeststatus, _requesteddate, _sourceid;

    public ManageRequestModel() {

    }

    public ManageRequestModel(String approvedcustdate, String approvecustid, String category, String custid, String description, String lineno, String maintask, String primarykey, String purpose, String requeststatus, String requesteddate, String sourceid)  {
        this._approvedcustdate	= approvedcustdate;
        this._approvecustid		= approvecustid;
        this._category			= category;
        this._custid			= custid;
        this._description		= description;
        this._lineno			= lineno;
        this._maintask			= maintask;
        this._requesteddate		= requesteddate;
        this._primarykey		= primarykey;
        this._purpose			= purpose;
        this._sourceid			= sourceid;
        this._requeststatus		= requeststatus;
    }

    public void setApprovedCustDate(String approvedcustdate) { this._approvedcustdate = approvedcustdate; }
    public void setApprovedCustId(String approvecustid) { this._approvecustid = approvecustid; }
    public void setCategory(String category) { this._category = category; }
    public void setCustId(String custid) { this._custid = custid; }
    public void setDescription(String description) { this._description = description; }
    public void setLineNo(String lineno) { this._lineno = lineno; }
    public void setMainTask(String maintask) { this._maintask = maintask; }
    public void setRequestDate(String requesteddate) { this._requesteddate = requesteddate; }
    public void setPrimaryKey(String primarykey) { this._primarykey = primarykey; }
    public void setPurpose(String purpose) { this._purpose = purpose; }
    public void setSourceId(String sourceid) { this._sourceid = sourceid; }
    public void setRequestStatus(String requeststatus) { this._requeststatus = requeststatus; }

    public String getApprovedCustDate() { return this._approvedcustdate; }
    public String getApprovedCustId() { return this._approvecustid; }
    public String getCategory() { return this._category; }
    public String getCustId() { return this._custid; }
    public String getDescription() { return this._description; }
    public String getLineNo() { return this._lineno; }
    public String getMainTask() { return this._maintask; }
    public String getRequestDate() { return this._requesteddate; }
    public String getPrimaryKey() { return this._primarykey; }
    public String getPurpose() { return this._purpose; }
    public String getSourceId() { return this._sourceid; }
    public String getRequestStatus() { return this._requeststatus; }

}