package lt.justplius.android.pavezikas.post;

import java.util.ArrayList;
import java.util.Calendar;

import lt.justplius.android.pavezikas.R;
import lt.justplius.android.pavezikas.common.HttpPostStringResponse;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class Post {
    private static final String TAG = "Post";

    // Data to be contained and verified
	private ArrayList<String> mCities;
	private Calendar mCurrentCalendar;
	private Calendar mLeavingCalendarFrom;
	private Calendar mLeavingCalendarTo;
	private String mLeavingAddress;
	private String mDroppingAddress;
	private String mRoute;
	
	// DB fields
	private String mDbUserId;
	private String mDbRouteID;
	private String mDbPhoneId;
	private String mDbSeatsAvailable;
	private String mDbMessage;
	private String mDbLeavingDate;
	private String mDbLeavingAddressId;
	private String mDbDroppingAddressId;
	private String mDbLeavingTimeFrom;
	private String mDbLeavingTimeTo;
	private String mDbPrice;
	private String mDbPostType;

	private ArrayList<NameValuePair> mNameValuePairs;
	private int mLeavingSeekBarHours;
	private int mLeavingSeekBarMinutes;
	private boolean mIsSeekBarActivated;
	    
    //Asynchronous tasks
    /*GetPhoneIdTask getPhoneIdTask;
    GetAddressIdTask getAddressIdTask;
    GetRouteIdTask getRouteIdTask;
    InsertPostTask insertPostTask;*/
    HttpPostStringResponse mResponse;
    String mResult;
    private Context mContext;
    private String mPhone;

    public Post(Context context){
		mContext = context;

        mCities = new ArrayList<>(2);
        mCities.add("Vilnius");
        mCities.add("Kaunas");

        mCurrentCalendar = Calendar.getInstance();
        mLeavingCalendarFrom = Calendar.getInstance();
        mLeavingCalendarTo = Calendar.getInstance();

        mIsSeekBarActivated = true;
        mLeavingSeekBarHours = 0;
        mLeavingSeekBarMinutes = 30;
		countDateAndTime();

		mDbSeatsAvailable = "4";
		mDbPrice = "20";

        mLeavingAddress = "";
        mDroppingAddress = "";

        // Initialize fields that can be empty / null in DB
        mDbLeavingAddressId = "0";
        mDbDroppingAddressId = "0";
        mDbPhoneId = "0";
        mDbMessage = "";
        mDbLeavingTimeTo = "";
        mDbRouteID = "0";
        mRoute = "";
        mPhone = "";
	}
	
	// Set post type: whether it is passenger or driver
	public void setPostType (String type) {
        mDbPostType = type;
	}

	public void setDate(long milliseconds){
        int hour = mCurrentCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = mCurrentCalendar.get(Calendar.MINUTE);
        mCurrentCalendar.setTimeInMillis(milliseconds);
        mCurrentCalendar.set(Calendar.HOUR_OF_DAY, hour);
        mCurrentCalendar.set(Calendar.MINUTE, minute);
		countDateAndTime();
	}

    public Calendar getCurrentCalendar(){
        return mCurrentCalendar;
    }

    public Calendar getLeavingCalendarFrom(){
        return mLeavingCalendarFrom;
    }

    public Calendar getLeavingCalendarTo(){
        return mLeavingCalendarTo;
    }

	// Set leaving time
	public void setTime(int hours, int minutes){
        mCurrentCalendar.set(Calendar.HOUR_OF_DAY, hours);
        mCurrentCalendar.set(Calendar.MINUTE, minutes);
		countDateAndTime();
	}

	// Evaluate leaving date and time keeping in mind, that interval of hours and minutes is chosen
	private void countDateAndTime(){
		// From date and time
        //noinspection ResourceType
		mLeavingCalendarFrom.set(mCurrentCalendar.get(Calendar.YEAR),
                mCurrentCalendar.get(Calendar.MONTH),
                mCurrentCalendar.get(Calendar.DAY_OF_MONTH),
                mCurrentCalendar.get(Calendar.HOUR_OF_DAY),
                mCurrentCalendar.get(Calendar.MINUTE));
		if (mIsSeekBarActivated) {
            mLeavingCalendarFrom.add(Calendar.HOUR_OF_DAY, -mLeavingSeekBarHours);
            mLeavingCalendarFrom.add(Calendar.MINUTE, -mLeavingSeekBarMinutes);
		}
		// To date and time
        //noinspection ResourceType
        mLeavingCalendarTo.set(mCurrentCalendar.get(Calendar.YEAR),
                mCurrentCalendar.get(Calendar.MONTH),
                mCurrentCalendar.get(Calendar.DAY_OF_MONTH),
                mCurrentCalendar.get(Calendar.HOUR_OF_DAY),
                mCurrentCalendar.get(Calendar.MINUTE));
		if (mIsSeekBarActivated) {
            mLeavingCalendarTo.add(Calendar.HOUR_OF_DAY, mLeavingSeekBarHours);
            mLeavingCalendarTo.add(Calendar.MINUTE, mLeavingSeekBarMinutes);
		}
	}

    // Set whether leaving time is flexible
    public void setIsFlexible(boolean isChecked) {
        mIsSeekBarActivated = isChecked;
        countDateAndTime();
    }

    public boolean getIsFlexible() {
        return mIsSeekBarActivated;
    }


	// Set interval for leaving time
	public void setTimeInterval(int hours, int minutes){
		mLeavingSeekBarHours = hours;
        mLeavingSeekBarMinutes = minutes;
		countDateAndTime();
	}

    public int getLeavingSeekBarMinutes() {
        return mLeavingSeekBarMinutes;
    }

    public int getLeavingSeekBarHours() {
        return mLeavingSeekBarHours;
    }

	public void setSeatsAvailable(String seatsAvailable) {
		mDbSeatsAvailable = seatsAvailable;
	}

	public String getSeatsAvailable() {
		return mDbSeatsAvailable;
	}

	public void setPrice(String price) {
		mDbPrice = price;
	}

	public String getPrice() {
		return mDbPrice;
	}

	public void setMessage(String message) {
		mDbMessage = message;
	}

	public String getMessage() {
		return mDbMessage;
	}	

	public void setPhone(String phone) {
		if (!phone.equals("86") && !phone.equals("+370") && !phone.equals("")) {
            mPhone = phone;
			new GetPhoneIdTask().execute(phone);
		} else {
			mDbPhoneId = "0";
		}
	}

    public String getPhoneId(){
        return mDbPhoneId;
    }

    public String getPhone() {
        return mPhone;
    }

    //TODO move to background thread and to separate class
    // Task to insert phone to DB and to retrieve it's id
    private class GetPhoneIdTask extends AsyncTask<String, Void, String> {
        private ArrayList<NameValuePair> mPairs;
        private String mUrl;

        protected void onPreExecute () {
            mPairs = new ArrayList<>();
            mUrl = mContext.getString(R.string.url_select_from_phone);
        }

        @Override
        protected String doInBackground(String... params) {
            // Check whether such phone exists, if it does not exist insert
            //  new one into db and return its' ID
            mPairs.add(new BasicNameValuePair("phone", params[0]));
            return new HttpPostStringResponse(mUrl, mPairs).returnJSON();
        }

        protected void onPostExecute(String result) {
            try{
                mDbPhoneId = String.valueOf(new JSONObject(result).getInt("id"));
            } catch(JSONException e){
                Log.e(TAG, "selectFromPhone.php error: ", e);
            }
        }
    }

	public void setLeavingAddress(String address) {
		mLeavingAddress = address;
        if (!address.equals("")) {
            new GetAddressIdTask().execute(address, "leaving address");
        } else {
            mDbLeavingAddressId = "0";
        }
	}

    public void setDroppingAddress(String address) {
        mDroppingAddress = address;
        if (!address.equals("")) {
            new GetAddressIdTask().execute(address, "dropping address");
        } else {
            mDbDroppingAddressId = "0";
        }
    }

    public boolean isLeavingAddressSet(){
        return !mLeavingAddress.equals("");
    }

    public boolean isDroppingAddressSet(){
        return !mDroppingAddress.equals("");
    }

	public String getLeavingAddress() {
		if (isLeavingAddressSet()){
            return mLeavingAddress;
		} else {
            return mCities.get(0);
		}
	}

	//return dropping address for TextView
	public String getDroppingAddress() {
        if (isDroppingAddressSet()){
            return mDroppingAddress;
        } else {
            return mCities.get(1);
        }
	}

    public String getRouteID() {
        return mDbRouteID;
    }

    public String getCity(int position) {
        return mCities.get(position);
    }

    public void setRouteCity(int index, String string) {
        // Same cities were set for leaving and dropping
        // Do nothing
        if (index == 0 && mCities.get(1).equals(string)
                || index == 1 && mCities.get(0).equals(string)
                )
            return;

        String oldRoute = mRoute;
        mCities.set(index, string);
		
		// Check whether it is new route and retrieve its id
		mRoute = mCities.get(0) + " - " + mCities.get(1);
        if (!oldRoute.equals(mRoute)) {
            new GetRouteIdTask().execute(mRoute);

            if (index == 0) {
                setLeavingAddress("");
            } else if (index == 1) {
                setDroppingAddress("");
            }
        }
	}

	//TODO move to background thread and to separate class
	// Task to insert address to DB and retrieve it's id
	private class GetAddressIdTask extends AsyncTask<String, Void, String> {
		private String mType;
        private ArrayList<NameValuePair> mPairs;
        private String mUrl;

        protected void onPreExecute () {
		    mPairs = new ArrayList<>();
		    mUrl = mContext.getString(R.string.url_select_from_address);
		}
		    	
		@Override
		protected String doInBackground(String... params) {
            mType = params[1];
			mPairs.add(new BasicNameValuePair("address", params[0]));
            return new HttpPostStringResponse(mUrl, mPairs).returnJSON();
		}
				
		protected void onPostExecute(String result) {
			try{
				String addressId = new JSONObject(result).getString("id");
		    	if(!addressId.equals("0")){
		    		if (mType.equals("leaving address")) {
		    	        mDbLeavingAddressId = addressId;
		    	    } else if (mType.equals("dropping address")) {
		    	        mDbDroppingAddressId = addressId;
		    	    }
		    	}
		    } catch(JSONException e){
		    	Log.e(TAG, "selectFromAddress.php error: ", e);
		    }    	    
		}
	}	

	//TODO move to background thread and to separate class
	// Task to insert route to DB and retrieve it's id
	private class GetRouteIdTask extends AsyncTask<String, Void, String> {

        private ArrayList<NameValuePair> mPairs;
        private String mUrl;

        protected void onPreExecute () {
		    mPairs = new ArrayList<>();
		    mUrl = mContext.getString(R.string.url_select_from_route);
		}
		    	
		@Override
		protected String doInBackground(String... params) {
			// Check whether such route exists, if it does not,
			// insert new one into DB and retrieve its' ID
			mPairs.add(new BasicNameValuePair("route", params[0]));
		    return new HttpPostStringResponse(mUrl, mPairs).returnJSON();
		}
				
		protected void onPostExecute(String result) {
		    try{
		    	String routeId = new JSONObject(result).getString("id");
		    	if(!routeId.equals("0")){
		    	    mDbRouteID = routeId;
		    	}
		    } catch(JSONException e){
		    	Log.e(TAG, "selectFromRoute.php error: ", e);
		    }    	    
		}
	}
/*
	//check if leaving address is set
	public void insetPost(){
		insertPostTask = new InsertPostTask();
		insertPostTask.execute();
	}*/

/*
    //TODO move to background thread and to separate class
	//Task to insert post to DB	
	private class InsertPostTask extends AsyncTask<Void, Void, String> {
				
		protected void onPreExecute () {    	
			
		    url = context.getString(R.string.url_insert_to_post);
		    
		    //prepare DB fields
		    //db_user_id
		    SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
			db_user_id = p.getString("FB_ID", "");
			
			//db_leaving_date
			sb = new StringBuilder();
			sb.append(leaving_calendar_from.get(Calendar.YEAR));
			sb.append("-");
			sb.append(leaving_calendar_from.get(Calendar.MONTH) + 1);
			sb.append("-");
			sb.append(leaving_calendar_from.get(Calendar.DAY_OF_MONTH));
			db_leaving_date = sb.toString();  	
			
			//db_leaving_time_from
			sb = new StringBuilder();
			sb.append(leaving_calendar_from.get(Calendar.HOUR_OF_DAY));
			sb.append(":");
			sb.append(leaving_calendar_from.get(Calendar.MINUTE));
			sb.append(":");
			sb.append(leaving_calendar_from.get(Calendar.SECOND));
			db_leaving_time_from = sb.toString();  	
			
			//db_leaving_time_to
			sb = new StringBuilder();
			sb.append(leaving_calendar_to.get(Calendar.HOUR_OF_DAY));
			sb.append(":");
			sb.append(leaving_calendar_to.get(Calendar.MINUTE));
			sb.append(":");
			sb.append(leaving_calendar_to.get(Calendar.SECOND));
			db_leaving_time_to = sb.toString();  	
			
			//prepare name value pairs data
    	    nameValuePairs = new ArrayList<NameValuePair>(); 	    	
    	    nameValuePairs.add(new BasicNameValuePair("user_id", db_user_id));
    	    nameValuePairs.add(new BasicNameValuePair("route_id", db_route_id));
    	    nameValuePairs.add(new BasicNameValuePair("phone_id", db_phone_id));
    	    nameValuePairs.add(new BasicNameValuePair("seats_available", db_seats_available));
    	    nameValuePairs.add(new BasicNameValuePair("message", db_message));
    	    nameValuePairs.add(new BasicNameValuePair("leaving_date", db_leaving_date));
    	    nameValuePairs.add(new BasicNameValuePair("leaving_time_from", db_leaving_time_from));
    	    nameValuePairs.add(new BasicNameValuePair("leaving_time_to", db_leaving_time_to));
    	    nameValuePairs.add(new BasicNameValuePair("price", db_price));
    	    nameValuePairs.add(new BasicNameValuePair("leaving_address_id", db_leaving_address_id));
    	    nameValuePairs.add(new BasicNameValuePair("dropping_address_id", db_dropping_address_id));  
    	    nameValuePairs.add(new BasicNameValuePair("post_type", db_post_type));  
    	    
		}
		    	
		@Override
		protected String doInBackground(Void... params) {		    
			//insert new post
            sdq = new ServerDbQuerry(nameValuePairs, url);
            result = sdq.returnJSON();
            
            //check if insert is successful 
    	    try{
    	    	
    	    	jsonObject = new JSONObject(result);	
    	    	
    	        if(jsonObject.getInt("id") > 0){    	 
    	            return "ok";
    	        } else {
    	            return "failed_insert";    	            
    	        }
    	        
    	    } catch(JSONException e){    	    	
    	    	Log.e("PostDataContainer.java", "insertToPost.php uknown error: "+e.toString());
    	    	return "failed_insert";
    	    }
		}
				
		protected void onPostExecute(String result) {
			//inform about success or failure
			sb = new StringBuilder();
			if (result.equals("ok")){
				sb.append(context.getString(R.string.successful_post_insert));
			} else if (result.equals("failed_insert")) {
				sb.append(context.getString(R.string.unsuccessful_post_insert));
			}
			Toast.makeText(context, sb.toString(), duration).show();    
		}
	}*/

}
