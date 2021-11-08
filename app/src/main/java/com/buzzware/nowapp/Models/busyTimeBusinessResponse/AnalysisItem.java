package com.buzzware.nowapp.Models.busyTimeBusinessResponse;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class AnalysisItem{

	@SerializedName("surge_hours")
	private SurgeHours surgeHours;

	@SerializedName("peak_hours")
	private List<PeakHoursItem> peakHours;

	@SerializedName("hour_analysis")
	private List<HourAnalysisItem> hourAnalysis;

	@SerializedName("day_info")
	private DayInfo dayInfo;

	@SerializedName("quiet_hours")
	private List<Integer> quietHours;

	@SerializedName("busy_hours")
	private List<Integer> busyHours;

	@SerializedName("day_raw")
	private List<Integer> dayRaw;

	public SurgeHours getSurgeHours(){
		return surgeHours;
	}

	public List<PeakHoursItem> getPeakHours(){
		return peakHours;
	}

	public List<HourAnalysisItem> getHourAnalysis(){
		return hourAnalysis;
	}

	public DayInfo getDayInfo(){
		return dayInfo;
	}

	public List<Integer> getQuietHours(){
		return quietHours;
	}

	public List<Integer> getBusyHours(){
		return busyHours;
	}

	public List<Integer> getDayRaw(){
		return dayRaw;
	}
}