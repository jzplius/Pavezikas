package lt.justplius.android.pavezikas.display_posts;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import android.content.Context;
import android.text.format.Time;

import static lt.justplius.android.pavezikas.common.PostUtils.getFormattedDate;

public class PostListItem {
	private int mId;
    private String mPrice;
	private float mRating;
	private int mSeatsAvailable;
	private String mRoute;
	private Time mDate;
	private Time mLeavingTimeFrom;
	private Time mLeavingTimeTo;
	private String mNameSurname;
    private String mUserId;
    private String mPostType;


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

    public String getDate(Context context) throws ParseException {
        return getFormattedDate(context, mDate.toMillis(false));
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

    public void setUserId(String userId) {
        mUserId = userId;
    }

    public String getUserId() {
        return mUserId;
    }

    public String getPostType() {
        return mPostType;
    }

    public void setPostType(String postType) {
        mPostType = postType;
    }

    public String getPrice() {
        return mPrice;
    }

    public void setPrice(String price) {
        mPrice = price;
    }
}