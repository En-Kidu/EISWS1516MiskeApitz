package models;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Applikationszeit {
	public String tag;
	public List<String> zeiten;

	public Applikationszeit(JsonObject applikationszeiten) {
		this.tag = applikationszeiten.get("tag").getAsString();
		JsonParser mParser = new JsonParser();
		JsonArray mZeiten = applikationszeiten.get("zeiten").getAsJsonArray();		
		this.zeiten = new ArrayList<String>();
		
		for (int i = 0; i < mZeiten.size(); i++) {
			JsonObject zeit = mZeiten.get(i).getAsJsonObject();
			this.zeiten.add(zeit.get("zeit").getAsString());
		}
	}
}
