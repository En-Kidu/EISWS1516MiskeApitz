package controller;

import com.example.mdks_client_pfleger.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import models.Verordnung;

public class VerordnungArrayAdapter extends ArrayAdapter {
	private final Context context;
	private final Verordnung[] verordnungen;

	public VerordnungArrayAdapter(Context context, Verordnung[] verordnungen) {
		super(context, R.layout.liste_medikationsplan, verordnungen);
		this.context = context;
		this.verordnungen = verordnungen;
	}
	
	@Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    LayoutInflater inflater = (LayoutInflater) context
	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView = inflater.inflate(R.layout.liste_medikationsplan, parent, false);
	    TextView uhrzeit = (TextView) rowView.findViewById(R.id.medlist_uhrzeit);
	    //uhrzeit.setText(verordnungen[position]);
	    // Change the icon for Windows and iPhone
	    

	    return rowView;
	  }

}
