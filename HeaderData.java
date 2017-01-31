package com.likecorp.app37;

public class HeaderData {
	int 	_id, _password, _password1, _startkm, _endkm, _otherexpenses;
	String 	_username, _username1, _finance, _area, _temp, _filename, _insertedon, _vehicleno, _collstarted;
	
	public HeaderData() {
	
	}
	
	public HeaderData(int password, int password1, String username, String username1, String finance, String area, String temp, String filename, String vehicleno, int startkm, int endkm, String collstarted, String insertedon, int otherexpenses) {
		this._password  	= password;
		this._password1		= password1;
		this._username		= username;
		this._username1		= username1;
		this._finance		= finance;
		this._area			= area;
		this._temp			= temp;
		this._filename		= filename;
		this._insertedon	= insertedon;
		this._startkm		= startkm;
		this._endkm			= endkm;
		this._vehicleno		= vehicleno;
		this._collstarted	= collstarted;
		this._otherexpenses = otherexpenses;
	}
	
	public void setCollStarted(String colstart) {
		this._collstarted = colstart;
	}
	
	public void setVehicleNo(String vehicleno) {
		this._vehicleno = vehicleno;
	}
	
	public void setStartKm(int startkm) {
		this._startkm = startkm;
	}
	
	public void setEndKm(int endkm) {
		this._endkm = endkm;
	}
	
	public void setOtherExpenses(int otherexpenses) {
		this._otherexpenses = otherexpenses;
	}
	
	public int getOtherExpenses() {
		return this._otherexpenses;
	}
	
	public String getCollStarted() {
		return this._collstarted;
	}
	
	public void setPassword(int password) {
		this._password = password;
	}
	
	public void setPassword1(int password1) {
		this._password1 = password1;
	}
	
	public void setUsername(String username){ 
		this._username = username;
	}
	
	public void setUsername1(String username1) {
		this._username1 = username1;
	}
	
	public void setFinance(String finance) {
		this._finance = finance;
	}
	
	public void setArea(String area) {
		this._area = area;
	}
	
	public void setFilename(String filename) {
		this._filename = filename;
	}
	
	public void setTemp(String temp) {
		this._temp = temp;
	}	
	
	public int getPassword() {
		return this._password;
	}
	
	public int getPassword1() {
		return this._password1;
	}
	
	public String getUsername() {
		return this._username;
	}
	
	public String getUsername1() {
		return this._username1;
	}
	
	public String getFinance() {
		return this._finance;
	}
	
	public String getArea() {
		return this._area;
	}
	
	public String getTemp() {
		return this._temp;
	}
	
	public String getFilename() {
		return this._filename;
	}
	
	public String getInsertedon() {
		return this._insertedon;
	}
	
	
	public String getVehicleNo() {
		return this._vehicleno;
	}
	
	public int getStartKm() {
		return this._startkm;
	}
	
	public int getEndKm() {
		return this._endkm;
	}
	
}