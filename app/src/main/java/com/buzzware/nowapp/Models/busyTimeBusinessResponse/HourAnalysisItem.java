package com.buzzware.nowapp.Models.busyTimeBusinessResponse;

import com.google.gson.annotations.SerializedName;

public class HourAnalysisItem{

	@SerializedName("hour")
	private int hour;

	@SerializedName("intensity_txt")
	private String intensityTxt;

	@SerializedName("intensity_nr")
	private String intensityNr;

	public int getHour(){
		return hour;
	}

	public String getIntensityTxt(){
		return intensityTxt;
	}

	public String getIntensityNr(){
		return intensityNr;
	}
}