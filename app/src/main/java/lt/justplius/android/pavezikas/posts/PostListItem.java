package lt.justplius.android.pavezikas.posts;

import java.sql.Date;
import java.text.SimpleDateFormat;
import android.text.format.Time;

public class PostListItem {
	private int mId;
	private float mRating;
	private int mSeatsAvailable;
	private String mRoute;
	private Time mDate;
	private Time mLeavingTimeFrom;
	private Time mLeavingTimeTo;
	private String mNameSurname;
    private String mUserId;

    public PostListItem() {
        mLeavingTimeFrom = new Time();
        mLeavingTimeTo = new Time();
        mDate = new Time();
	}
		
	public void setRating(float rating){
        mRating = rating;
	}
				
	public void setRouteInformation(String route){
        mRoute = route;
	}
	
	public void setId(int id){
        mId = id;
	}
				
	public void setDate(String date) throws java.text.ParseException{
        mDate.set(new SimpleDateFormat("yyyy-MM-dd").parse(date).getTime());
	}
				
	public void setLeavingTimeFrom(String time) throws java.text.ParseException{
        mLeavingTimeFrom.set(new SimpleDateFormat("HH:mm:ss").parse(time).getTime());
	}
		
	public void setLeavingTimeTo(String time) throws java.text.ParseException{
        mLeavingTimeTo.set(new SimpleDateFormat("HH:mm:ss").parse(time).getTime());
	}
				
	public void setSeatsAvailable(int seatsAvailable){
        mSeatsAvailable = seatsAvailable;
	}
	
	public void setName(String nameSurname){
        mNameSurname = nameSurname;
	}
		
	public float getRating(){
	   	return mRating;
	}
				
	public String getRouteInformation(){
	   	return mRoute;
	}
				
	public String getDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM");
        return (sdf.format(new Date(mDate.toMillis(false))) + " " + mDate.monthDay + " d.");
	}
				
	public String getTime(){
		StringBuilder timeString = new StringBuilder();		
		timeString
                .append(mLeavingTimeFrom.hour)
                .append(":");
		if (mLeavingTimeFrom.minute < 10){
			timeString.append("0"); 
		}
		timeString
                .append(mLeavingTimeFrom.minute)
                .append(" - ")
                .append(mLeavingTimeTo.hour)
                .append(":");
		if (mLeavingTimeTo.minute < 10){
			timeString.append("0"); 
		}
		timeString.append(mLeavingTimeTo.minute);
	   	return timeString.toString();
	}
				
	public String getSeatsAvailable(){
	   	return String.valueOf(mSeatsAvailable);
	}
	
	public int getId(){
	   	return mId;
	}
	
	public String getNameSurname(){
	   	return mNameSurname;
	}

    public void setUserId(String userId) {
        mUserId = userId;
    }

    public String getUserId() {
        return mUserId;
    }
}