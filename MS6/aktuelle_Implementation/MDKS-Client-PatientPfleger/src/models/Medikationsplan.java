package models;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Medikationsplan {
	public int station_id;
	public List<Verordnung> verordnungen;
	public List<TimeMap> verordnungTimes;
	public List<TimeMap> selbstverordnungTimes;

	public Medikationsplan(JsonArray verordnungen) {
		this.verordnungen = new ArrayList<Verordnung>();
		for (int i = 0; i < verordnungen.size(); i++) {
			JsonObject verordnung = verordnungen.get(i).getAsJsonObject();
			this.verordnungen.add(new Verordnung(verordnung));
		}
		this.verordnungTimes = makeVerordnungTimeMap();
	}

	public List<TimeMap> makeVerordnungTimeMap() {
		List<Integer> times = new ArrayList<Integer>();
		List<Integer> ids = new ArrayList<Integer>();

		for (int i = 0; i < this.verordnungen.size(); i++) {
			if (!this.verordnungen.get(i).selbstverordnung) {

				
				String currentDay = "Montag"; // TODO: getSystemZeit getTag
				for (int j = 0; j < this.verordnungen.get(i).applikationszeit.size(); j++) {
					String tag = this.verordnungen.get(i).applikationszeit.get(j).tag;
					if (tag.equals(currentDay)) {
						for (int k = 0; k < this.verordnungen.get(i).applikationszeit.get(j).zeiten.size(); k++) {
							String timeString = this.verordnungen.get(i).applikationszeit.get(j).zeiten.get(k);
							timeString = timeString.replace(":", "");
							int time = Integer.parseInt(timeString);
							times.add(time);
							ids.add(this.verordnungen.get(i).verordnung_id);
						}
					}
				}
			}
		}

		return sortTimeMap(times, ids);
	}

	private List<TimeMap> sortTimeMap(List<Integer> times, List<Integer> ids) {
		List<TimeMap> map = new ArrayList<TimeMap>();
		while (!times.isEmpty()) {
			int index = 0;
			int min = times.get(0);
			for (int i = 0; i < times.size(); i++) {
				if (min > times.get(i)) {
					index = i;
				}
			}

			map.add(new TimeMap(ids.get(index), times.get(index)));
			times.remove(index);
			ids.remove(index);

		}
		return map;
	}

	public Verordnung getVerordnung(int id) {
		for (int i = 0; i < verordnungen.size(); i++) {
			if (verordnungen.get(i).verordnung_id == id) {
				return verordnungen.get(i);
			}
		}
		return null;
	}

	public List<TimeMap> getTimesByPatientID(int id) {
		List<Integer> times = new ArrayList<Integer>();
		List<Integer> ids = new ArrayList<Integer>();

		for (int i = 0; i < this.verordnungen.size(); i++) {
			if (!this.verordnungen.get(i).selbstverordnung) {

				String currentDay = "Montag"; // TODO: getSystemZeit getTag
				for (int j = 0; j < this.verordnungen.get(i).applikationszeit.size(); j++) {
					String tag = this.verordnungen.get(i).applikationszeit.get(j).tag;
					if (tag.equals(currentDay)) {
						for (int k = 0; k < this.verordnungen.get(i).applikationszeit.get(j).zeiten.size(); k++) {
							String timeString = this.verordnungen.get(i).applikationszeit.get(j).zeiten.get(k);
							timeString = timeString.replace(":", "");
							int time = Integer.parseInt(timeString);
							times.add(time);
							ids.add(this.verordnungen.get(i).verordnung_id);
						}
					}
				}
			}
		}

		return sortTimeMap(times, ids);
	}
}
