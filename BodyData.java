package com.likecorp.app37;

public class BodyData {
	int _id, _accountno, _outstanding, _currdue, _paiddue;
	String _cname, _mobile, _openingdate;
	boolean _enadisable, _paidstatus, _accclosed;
	
	//Empty Constructor
	public BodyData() {
		
	}
	
	public BodyData(int accountno, int outstanding, int currdue, int paiddue, String cname, String mobile, String openingdate) {
		//this._id			= id;
		this._accountno 	= accountno; 
		this._outstanding 	= outstanding; 
		this._currdue		= currdue;
		this._paiddue		= paiddue;
		
		this._cname			= cname;
		this._mobile		= mobile;
		this._openingdate	= openingdate;
	}

	public void setCName(String name) {
		this._cname			= name;
	}
	
	public void setMobile(String mobile) {
		this._mobile		= mobile;
	}
	
	public void setOpeningDate(String openingdate) {
		this._openingdate	= openingdate;
	}
	
	public void setId(int id) {
		this._id	= id;
	}
	
	public void setAccountno(int accountno) {
    	this._accountno = accountno;
    }
	
	public void setOutstanding(int outstanding) {
    	this._outstanding = outstanding;
    }
	
	public void setCurrentDue(int currdue) {
    	this._currdue = currdue;
    }
	
	public void setEnaDisable(boolean enadisable) {
		this._enadisable = enadisable;
	}
	
	public void setPaidDue(int paiddue) {
		this._paiddue 	= paiddue;
	}
	
	public void setPaidStatus(boolean paidflag) {
		this._paidstatus  = paidflag;
	}
	
	public void setAccClosed(boolean accclosed) {
		this._accclosed	  = accclosed;
	}
	
	public boolean getAccClosed() {
		return this._accclosed;
	}
	
    public String getCName(){
        return this._cname;
    }
    
    public String getMobile() {
		return this._mobile;
	}
    
    public String getOpeningDate() {
    	return this._openingdate;
    }
    
    public int getId() {
    	return this._id;
    }
    
    public int getAccountno() {
    	return this._accountno;
    }
    
    public int getOutstanding() {
    	return this._outstanding;
    }
    
    public int getCurrentDue() {
    	return this._currdue;
    }
    
    public int getPaidDue() {
    	return this._paiddue;
    }
    
    public boolean getEnaDisable() {
    	return this._enadisable;
    }
    
    public boolean getPaidStatus() {
    	return this._paidstatus;
    }
}
