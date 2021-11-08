package com.buzzware.nowapp.Models.busyTimeBusinessResponse;

import com.google.gson.annotations.SerializedName;

public class SurgeHours{

	@SerializedName("most_people_leave")
	private int mostPeopleLeave;

	@SerializedName("most_people_come")
	private int mostPeopleCome;

	public int getMostPeopleLeave(){
		return mostPeopleLeave;
	}

	public int getMostPeopleCome(){
		return mostPeopleCome;
	}
}