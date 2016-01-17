package models;

public class TimeMap {
	public int id; // VerordnungsID
	public int time; // Applikationszeit in int, zum sortieren
	
	public TimeMap(int id,int time){
		this.id = id; 
		this.time = time; 
	}

	// get Applikationszeit in String
	public String getTimeString() {
		String tmp = String.valueOf(this.time);
		String time = null;
		if(tmp.length()<4){
			time = "0"+tmp.charAt(0)+":"+tmp.charAt(1)+tmp.charAt(2);
		}else{
			time = tmp.charAt(0)+""+tmp.charAt(1)+":"+tmp.charAt(2)+tmp.charAt(3);
		}
		
		return time;
	}
}
