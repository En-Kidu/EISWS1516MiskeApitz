package models;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.util.Log;

public class Verordnung {
	public int patient_id;
	public int station_id;
	public int verordnung_id;
	public int zimmer;
	public String beginn;
	public String ende;
	public String patientName;
	public String patientVorname;
	public List<Applikationszeit> applikationszeit;
	
	public Verordnung(JsonObject verordnung){
		this.patient_id = verordnung.get("patient_id").getAsInt();
		this.station_id = verordnung.get("station_id").getAsInt();
		this.verordnung_id = verordnung.get("verordnung_id").getAsInt();
		this.beginn = verordnung.get("beginn").getAsString();
		this.ende = verordnung.get("ende").getAsString();
		this.patientName = verordnung.get("name").getAsString();
		this.patientVorname = verordnung.get("vorname").getAsString();
		this.zimmer = verordnung.get("zimmer").getAsInt();
		
		JsonParser mParser = new JsonParser();
		JsonElement obj = mParser.parse(verordnung.get("applikationszeitpunkt").getAsString());
		JsonArray applikationszeiten = obj.getAsJsonArray();
		this.applikationszeit = new ArrayList<Applikationszeit>();
		for (int i = 0; i < applikationszeiten.size(); i++) {
			this.applikationszeit.add(new Applikationszeit(applikationszeiten.get(i).getAsJsonObject()));
		}
	}
}
