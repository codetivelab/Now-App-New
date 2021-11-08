package com.buzzware.nowapp.Models.busyTimeBusinessResponse;

import com.google.gson.annotations.SerializedName;

public class DayInfo{

	@SerializedName("day_rank_mean")
	private int dayRankMean;

	@SerializedName("venue_closed")
	private int venueClosed;

	@SerializedName("day_mean")
	private int dayMean;

	@SerializedName("day_rank_max")
	private int dayRankMax;

	@SerializedName("venue_open")
	private int venueOpen;

	@SerializedName("day_int")
	private int dayInt;

	@SerializedName("day_max")
	private int dayMax;

	@SerializedName("day_text")
	private String dayText;

	public int getDayRankMean(){
		return dayRankMean;
	}

	public int getVenueClosed(){
		return venueClosed;
	}

	public int getDayMean(){
		return dayMean;
	}

	public int getDayRankMax(){
		return dayRankMax;
	}

	public int getVenueOpen(){
		return venueOpen;
	}

	public int getDayInt(){
		return dayInt;
	}

	public int getDayMax(){
		return dayMax;
	}

	public String getDayText(){
		return dayText;
	}
}