package models;

import java.util.List;

import com.google.gson.JsonArray;

public class Medikationsplan {
	public int station_id;
	public List<Verordnung> verordnungen;
	
	public void addVerordnungen(JsonArray verordnungen){
		
	}
}
