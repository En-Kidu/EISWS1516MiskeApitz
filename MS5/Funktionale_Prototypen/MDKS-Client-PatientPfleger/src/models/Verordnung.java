package models;

import java.util.List;

import com.google.gson.JsonObject;

public class Verordnung {
	public int patient_id;
	public int station_id;
	public int verordnung_id;
	public List<Applikationszeit> applikationszeit;
	public String beginn;
	public String ende;
	public String patientName;
	public String patientVorname;
	public String zimmer;
	
	public void setVerordnung(JsonObject verordnung){
		
	}
}
