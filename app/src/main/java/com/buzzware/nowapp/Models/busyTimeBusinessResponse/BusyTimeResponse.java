package com.buzzware.nowapp.Models.busyTimeBusinessResponse;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class BusyTimeResponse{

	@SerializedName("api_key_private")
	private String apiKeyPrivate;

	@SerializedName("epoch_analysis")
	private String epochAnalysis;

	@SerializedName("venue_info")
	private VenueInfo venueInfo;

	@SerializedName("analysis")
	private List<AnalysisItem> analysis;

	@SerializedName("status")
	private String status;

	public String getApiKeyPrivate(){
		return apiKeyPrivate;
	}

	public String getEpochAnalysis(){
		return epochAnalysis;
	}

	public VenueInfo getVenueInfo(){
		return venueInfo;
	}

	public List<AnalysisItem> getAnalysis(){
		return analysis;
	}

	public String getStatus(){
		return status;
	}
}