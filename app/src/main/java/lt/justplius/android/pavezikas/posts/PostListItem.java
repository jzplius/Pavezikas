package lt.justplius.android.pavezikas.posts;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.res.Resources;
import android.text.format.Time;

import lt.justplius.android.pavezikas.R;

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
    private char mPostType;

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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = sdf.format(new Date(mDate.toMillis(false)));
        return getFormattedDate(context, formattedDate);
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

    public static String getFormattedDate(Context context, String dateString) throws ParseException {
        Time time = new Time();
        time.set(new SimpleDateFormat("yyyy-MM-dd").parse(dateString).getTime());
        StringBuilder date = new StringBuilder();
        Resources res = context.getResources();
        switch (time.month){
            case 0:
                date.append(res.getString(R.string.month_1_ltu));
                break;
            case 1:
                date.append(res.getString(R.string.month_2_ltu));
                break;
            case 2:
                date.append(res.getString(R.string.month_3_ltu));
                break;
            case 3:
                date.append(res.getString(R.string.month_4_ltu));
                break;
            case 4:
                date.append(res.getString(R.string.month_5_ltu));
                break;
            case 5:
                date.append(res.getString(R.string.month_6_ltu));
                break;
            case 6:
                date.append(res.getString(R.string.month_7_ltu));
                break;
            case 7:
                date.append(res.getString(R.string.month_8_ltu));
                break;
            case 8:
                date.append(res.getString(R.string.month_9_ltu));
                break;
            case 9:
                date.append(res.getString(R.string.month_10_ltu));
                break;
            case 10:
                date.append(res.getString(R.string.month_11_ltu));
                break;
            case 11:
                date.append(res.getString(R.string.month_12_ltu));
                break;
        }
        date.append(" ")
                .append(time.monthDay)
                .append(" d.");
        return date.toString();
    }

    public static String getFormattedSeats(Context context, String seatsString){
        String seats = context.getResources().getString(R.string.seats);
        return seats + seatsString;
    }

    public static String getFormattedPrice(Context context, String priceString){
        Resources res = context.getResources();
        String price = res.getString(R.string.price);
        String currency = res.getString(R.string.currency);
        return price + priceString + currency;
    }

    public char getPostType() {
        return mPostType;
    }

    public void setPostType(char postType) {
        mPostType = postType;
    }
}