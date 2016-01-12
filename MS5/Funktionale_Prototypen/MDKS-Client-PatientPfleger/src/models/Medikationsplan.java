package models;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Medikationsplan {
	public int station_id;
	public List<Verordnung> verordnungen;
	
	public Medikationsplan(JsonArray verordnungen){
		for (int i = 0; i < verordnungen.size(); i++) {
			JsonObject verordnung = verordnungen.get(i).getAsJsonObject();
			this.verordnungen = new ArrayList<Verordnung>();
			this.verordnungen.add(new Verordnung(verordnung));
		}
	}
}
