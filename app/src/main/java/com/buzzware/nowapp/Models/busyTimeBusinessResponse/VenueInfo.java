package com.buzzware.nowapp.Models.busyTimeBusinessResponse;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class VenueInfo{

	@SerializedName("venue_name")
	private String venueName;

	@SerializedName("venue_dwell_time_min")
	private int venueDwellTimeMin;

	@SerializedName("venue_dwell_time_avg")
	private int venueDwellTimeAvg;

	@SerializedName("venue_address")
	private String venueAddress;

	@SerializedName("venue_dwell_time_max")
	private int venueDwellTimeMax;

	@SerializedName("venue_type")
	private String venueType;

	@SerializedName("venue_lon")
	private double venueLon;

	@SerializedName("venue_types")
	private List<String> venueTypes;

	@SerializedName("venue_timezone")
	private String venueTimezone;

	@SerializedName("venue_lat")
	private double venueLat;

	@SerializedName("venue_id")
	private String venueId;

	public String getVenueName(){
		return venueName;
	}

	public int getVenueDwellTimeMin(){
		return venueDwellTimeMin;
	}

	public int getVenueDwellTimeAvg(){
		return venueDwellTimeAvg;
	}

	public String getVenueAddress(){
		return venueAddress;
	}

	public int getVenueDwellTimeMax(){
		return venueDwellTimeMax;
	}

	public String getVenueType(){
		return venueType;
	}

	public double getVenueLon(){
		return venueLon;
	}

	public List<String> getVenueTypes(){
		return venueTypes;
	}

	public String getVenueTimezone(){
		return venueTimezone;
	}

	public double getVenueLat(){
		return venueLat;
	}

	public String getVenueId(){
		return venueId;
	}
}