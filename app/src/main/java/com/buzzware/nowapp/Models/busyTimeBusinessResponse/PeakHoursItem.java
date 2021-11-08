package com.buzzware.nowapp.Models.busyTimeBusinessResponse;

import com.google.gson.annotations.SerializedName;

public class PeakHoursItem{

	@SerializedName("peak_end")
	private int peakEnd;

	@SerializedName("peak_start")
	private int peakStart;

	@SerializedName("peak_intensity")
	private int peakIntensity;

	@SerializedName("peak_delta_mean_week")
	private int peakDeltaMeanWeek;

	@SerializedName("peak_max")
	private int peakMax;

	public int getPeakEnd(){
		return peakEnd;
	}

	public int getPeakStart(){
		return peakStart;
	}

	public int getPeakIntensity(){
		return peakIntensity;
	}

	public int getPeakDeltaMeanWeek(){
		return peakDeltaMeanWeek;
	}

	public int getPeakMax(){
		return peakMax;
	}
}