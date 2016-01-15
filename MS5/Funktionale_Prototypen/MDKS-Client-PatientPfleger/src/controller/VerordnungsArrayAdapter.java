package controller;

import java.util.List;

import com.example.mdks_client_pfleger.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import models.Medikationsplan;
import models.TimeMap;
import models.Verordnung;

public class VerordnungsArrayAdapter extends ArrayAdapter<TimeMap> {
	private final Context context;
	private final List<TimeMap> map;
	private final Medikationsplan mPlan;

	public VerordnungsArrayAdapter(Context context, int ressource, List<TimeMap> map,Medikationsplan mPlan) {
		super(context, ressource, map);
		this.context = context;
		this.map = map;
		this.mPlan = mPlan;
		
	}
	
	@Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    LayoutInflater inflater = (LayoutInflater) context
	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView = inflater.inflate(R.layout.liste_medikationsplan, parent, false);
	    int id = map.get(position).id;
	    
	    TextView name = (TextView) rowView.findViewById(R.id.medlist_name);
	    name.setText(mPlan.getVerordnung(id).patientName+"");
	    
	    TextView station = (TextView) rowView.findViewById(R.id.medlist_station);
	    station.setText(mPlan.getVerordnung(id).station_id+"");
	    
	    TextView zimmer = (TextView) rowView.findViewById(R.id.medlist_zimmer);
	    zimmer.setText(mPlan.getVerordnung(id).zimmer+"");
	    
	    TextView applikationsweg = (TextView) rowView.findViewById(R.id.medlist_applikationsweg);
	    applikationsweg.setText(mPlan.getVerordnung(id).applikationsweg+"");
	    
	    TextView einheit = (TextView) rowView.findViewById(R.id.medlist_einheit);
	    einheit.setText(mPlan.getVerordnung(id).einheit+"");
	    
	    TextView medikament = (TextView) rowView.findViewById(R.id.medlist_medikament);
	    medikament.setText(mPlan.getVerordnung(id).medikament+"");
	    
	    TextView dosierung = (TextView) rowView.findViewById(R.id.medlist_dosierung);
	    dosierung.setText(mPlan.getVerordnung(id).dosierung+"");
	    
	    TextView uhrzeit = (TextView) rowView.findViewById(R.id.medlist_uhrzeit);
	    uhrzeit.setText(map.get(position).time+"");
	    
	    return rowView;
	  }

}
