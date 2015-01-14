package lt.justplius.android.pavezikas.add_post;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import android.support.v4.app.LoaderManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import lt.justplius.android.pavezikas.add_post.events.DownloadFinishedEvent;
import lt.justplius.android.pavezikas.add_post.events.DownloadStartedEvent;
import lt.justplius.android.pavezikas.mangers.DownloadsManager;
import lt.justplius.android.pavezikas.mangers.BusManager;
import lt.justplius.android.pavezikas.mangers.LoadersManager;

import static lt.justplius.android.pavezikas.mangers.LoadersManager.LOADER_DROPPING_ADDRESS_ID;
import static lt.justplius.android.pavezikas.mangers.LoadersManager.LOADER_LEAVING_ADDRESS_ID;
import static lt.justplius.android.pavezikas.mangers.LoadersManager.LOADER_MESSAGE_ID;
import static lt.justplius.android.pavezikas.mangers.LoadersManager.LOADER_PHONE_ID;
import static lt.justplius.android.pavezikas.mangers.LoadersManager.LOADER_ROUTE_ID;

public class Post
        implements LoaderManager.LoaderCallbacks<String> {
    private static final String TAG = "Post";
    private static final String ARG_ADDRESS = "address";
    private static final String ARG_ROUTE = "route";
    private static final String ARG_PHONE = "phone";
    private static final String ARG_MESSAGE = "message";

    // Data to be contained and verified
	private ArrayList<String> mCities;
	private Calendar mCurrentCalendar;
	private Calendar mLeavingCalendarFrom;
	private Calendar mLeavingCalendarTo;
	private String mLeavingAddress;
	private String mDroppingAddress;
	private String mRoute;
    private String mPhone;
    private String mMessage;

	// Fields used in DB to describe post
    private String mDbUserId;
	private String mDbRouteId;
	private String mDbPhoneId;
	private String mDbSeatsAvailable;
	private String mDbMessageId;
	private String mDbLeavingDate;
	private String mDbLeavingAddressId;
	private String mDbDroppingAddressId;
	private String mDbLeavingTimeFrom;
	private String mDbLeavingTimeTo;
	private String mDbPrice;
	private String mDbPostType;
    private String mDbPostId;

	private int mLeavingSeekBarHours;
	private int mLeavingSeekBarMinutes;
	private boolean mIsSeekBarActivated;

    private boolean mIsFirstSpinnerSet;
    private Context mContext;

    private static Post sPost;

    // Get post instance
    public static Post getInstance() {
        if (sPost == null) {
            sPost = new Post();
        }
        return sPost;
    }

    private Post() {
        mCurrentCalendar = Calendar.getInstance();
        mCurrentCalendar.set(Calendar.MINUTE, 0);
        mCurrentCalendar.set(Calendar.HOUR, 6);
        mCurrentCalendar.add(Calendar.HOUR, 12);
        mLeavingCalendarFrom = Calendar.getInstance();
        mLeavingCalendarTo = Calendar.getInstance();

        mCities = new ArrayList<>(2);
        mCities.add("Vilnius");
        mCities.add("Kaunas");

        mIsSeekBarActivated = true;
        mLeavingSeekBarHours = 2;
        mLeavingSeekBarMinutes = 0;
		countDateAndTime();

        // Initialize fields that can be empty / null in DB
        mDbSeatsAvailable = "4";
        mDbPrice = "20";
        mDbLeavingAddressId = "0";
        mDbDroppingAddressId = "0";
        mDbPhoneId = "0";
        mDbMessageId = "0";
        mDbLeavingTimeTo = "";
        mDbRouteId = "0";
        mDbPostType = "driver";

        mRoute = "";
        mPhone = "";
        mMessage = "";
        mLeavingAddress = "";
        mDroppingAddress = "";

        mIsFirstSpinnerSet = false;
	}

    // Set post type: whether it is passenger or driver
	public void setPostType (String type) {
        mDbPostType = type;
	}

    public String getPostType() {
        return mDbPostType;
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

	public void setMessage(String message, Context context) {
        mContext = context;

        if (!message.equals("")) {
            if (!mMessage.equals(message)) {
                mMessage = message;
                Bundle args = new Bundle();
                args.putString(ARG_MESSAGE, mMessage);

                // Post an event, informing to start loading message Id
                BusManager
                        .getInstance()
                        .post(new DownloadStartedEvent(LOADER_MESSAGE_ID));

                ((FragmentActivity) context)
                        .getSupportLoaderManager()
                        .restartLoader(LOADER_MESSAGE_ID, args, this);
            }
        } else {
            mDbMessageId = "0";
            mMessage = "";
            if (DownloadsManager.isBeingDownloaded(LOADER_MESSAGE_ID)) {
                // Post an event, informing that message Id has finished loading
                BusManager
                        .getInstance()
                        .post(new DownloadFinishedEvent(LOADER_MESSAGE_ID));
            }
        }
	}

	public String getMessage() {
		return mMessage;
	}	

	public void setPhone(String phone, Context context) {
        mContext = context;

		if (phone.length() >= 8) {
            if (!mPhone.equals(phone)) {
                mPhone = phone;
                Bundle args = new Bundle();
                args.putString(ARG_PHONE, mPhone);

                // Post an event, informing to start loading phone Id
                BusManager
                        .getInstance()
                        .post(new DownloadStartedEvent(LOADER_PHONE_ID));

                ((FragmentActivity) context)
                        .getSupportLoaderManager()
                        .restartLoader(LOADER_PHONE_ID, args, this);
            }
        } else {
			mDbPhoneId = "0";
            mPhone = "+370";
            if (DownloadsManager.isBeingDownloaded(LOADER_PHONE_ID)) {
                // Post an event, informing that phone Id has finished loading
                BusManager
                        .getInstance()
                        .post(new DownloadFinishedEvent(LOADER_PHONE_ID));
            }
        }
	}

    public String getPhoneId(){
        return mDbPhoneId;
    }

    public String getPhone() {
        return mPhone;
    }

	public void setLeavingAddress(String address, Context context) {
		mLeavingAddress = address;
        mContext = context;

        if (!address.equals("")) {
            Bundle args = new Bundle();
            args.putString(ARG_ADDRESS, address);

            // Post an event, informing to start loading address Id
            BusManager
                    .getInstance()
                    .post(new DownloadStartedEvent(LOADER_LEAVING_ADDRESS_ID));

            ((FragmentActivity) context)
                    .getSupportLoaderManager()
                    .restartLoader(LOADER_LEAVING_ADDRESS_ID, args, this);
        } else {
            mDbLeavingAddressId = "0";
            mLeavingAddress = "";

            // Post an event, informing that address Id has finished loading
            BusManager
                    .getInstance()
                    .post(new DownloadFinishedEvent(LOADER_LEAVING_ADDRESS_ID));
        }
	}

    public void setDroppingAddress(String address, Context context) {
        mDroppingAddress = address;
        mContext = context;

        if (!address.equals("")) {
            Bundle args = new Bundle();
            args.putString(ARG_ADDRESS, address);

            // Post an event, informing to start loading address Id
            BusManager
                    .getInstance()
                    .post(new DownloadStartedEvent(LOADER_DROPPING_ADDRESS_ID));

            ((FragmentActivity) context)
                    .getSupportLoaderManager()
                    .restartLoader(LOADER_DROPPING_ADDRESS_ID, args, this);
        } else {
            mDbDroppingAddressId = "0";
            mDroppingAddress = "";

            // Post an event, informing that address Id has finished loading
            BusManager
                    .getInstance()
                    .post(new DownloadFinishedEvent(LOADER_DROPPING_ADDRESS_ID));
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
        return mDbRouteId;
    }

    public String getCity(int position) {
        return mCities.get(position);
    }

    public boolean isFirstSpinnerSet() {
        return mIsFirstSpinnerSet;
    }

    public void setFirstSpinnerSet(boolean isFirstSpinnerSet) {
        mIsFirstSpinnerSet = isFirstSpinnerSet;
    }

    public void setRouteCity(int index, String string, Context context) {
        // Same cities were set for leaving and dropping.
        // Do nothing and disable selected Facebook groups
        if (index == 0 && mCities.get(1).equals(string)
                || index == 1 && mCities.get(0).equals(string)
                ) {
            mDbRouteId = "0";
            mRoute = "";

            // Post an event, informing that route ID was loaded
            BusManager
                    .getInstance()
                    .post(new DownloadFinishedEvent(LOADER_ROUTE_ID));

            if (index == 0) {
                setLeavingAddress("", null);
                mCities.set(0, mCities.get(1));
            } else {
                setDroppingAddress("", null);
                mCities.set(1, mCities.get(0));
            }
            return;
        }

        String oldRoute = mRoute;
        mCities.set(index, string);
		
		// Check whether it is new route and retrieve it's id
		String route = mCities.get(0) + " - " + mCities.get(1);
        if (!oldRoute.equals(route) && isFirstSpinnerSet()) {
            mRoute = route;
            mDbRouteId = "0";
            Bundle args = new Bundle();
            args.putString(ARG_ROUTE, mRoute);

            // Post an event, informing that route ID is being loaded
            BusManager.getInstance().post(new DownloadStartedEvent(LOADER_ROUTE_ID));
            mContext = context;
            ((FragmentActivity) context)
                    .getSupportLoaderManager()
                    .restartLoader(LOADER_ROUTE_ID, null, this);

            if (index == 0) {
                setLeavingAddress("", null);
            } else if (index == 1) {
                setDroppingAddress("", null);
            }
        // Trigger to prevent download same result twice, on view inflating two similar spinners
        } else {
            setFirstSpinnerSet(true);
        }
	}

    public String getRoute() {
        return mRoute;
    }

    public String getMessageId() {
        return mDbMessageId;
    }

    public String getLeavingAddressId() {
        return mDbLeavingAddressId;
    }

    public String getDroppingAddressId() {
        return mDbDroppingAddressId;
    }

    public String getLeavingDate() {
        //noinspection StringBufferReplaceableByString
        StringBuilder sb = new StringBuilder();
        sb.append(mLeavingCalendarFrom.get(Calendar.YEAR));
        sb.append("-");
        sb.append(mLeavingCalendarFrom.get(Calendar.MONTH) + 1);
        sb.append("-");
        sb.append(mLeavingCalendarFrom.get(Calendar.DAY_OF_MONTH));
        mDbLeavingDate = sb.toString();
        return mDbLeavingDate;
    }

    public String getLeavingTimeFrom() {
        //noinspection StringBufferReplaceableByString
        StringBuilder sb = new StringBuilder();
        sb = new StringBuilder();
        sb.append(mLeavingCalendarFrom.get(Calendar.HOUR_OF_DAY));
        sb.append(":");
        sb.append(mLeavingCalendarFrom.get(Calendar.MINUTE));
        sb.append(":");
        sb.append(mLeavingCalendarFrom.get(Calendar.SECOND));
        mDbLeavingTimeFrom = sb.toString();
        return mDbLeavingTimeFrom;
    }

    public String getLeavingTimeTo() {
        //noinspection StringBufferReplaceableByString
        StringBuilder sb = new StringBuilder();
        sb = new StringBuilder();
        sb.append(mLeavingCalendarTo.get(Calendar.HOUR_OF_DAY));
        sb.append(":");
        sb.append(mLeavingCalendarTo.get(Calendar.MINUTE));
        sb.append(":");
        sb.append(mLeavingCalendarTo.get(Calendar.SECOND));
        mDbLeavingTimeTo = sb.toString();
        return mDbLeavingTimeTo;
    }

    public String getPostId() {
        return mDbPostId;
    }

    public void setPostId(String dbPostId) {
        mDbPostId = dbPostId;
    }

    @Override
    public android.support.v4.content.Loader<String> onCreateLoader(int id, Bundle args) {
        switch (id){
            case LOADER_LEAVING_ADDRESS_ID:
                return LoadersManager
                        .getInstance(mContext)
                        .createAddressIdLoader(args.getString(ARG_ADDRESS));
            case LOADER_DROPPING_ADDRESS_ID:
                return LoadersManager
                        .getInstance(mContext)
                        .createAddressIdLoader(args.getString(ARG_ADDRESS));
            case LOADER_ROUTE_ID:
                return LoadersManager
                        .getInstance(mContext)
                        .createRouteIdLoader();
            case LOADER_PHONE_ID:
                return LoadersManager
                        .getInstance(mContext)
                        .createPhoneIdLoader(args.getString(ARG_PHONE));
            case LOADER_MESSAGE_ID:
                return LoadersManager
                        .getInstance(mContext)
                        .createMessageIdLoader(args.getString(ARG_MESSAGE));
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<String> loader, String data) {
        switch (loader.getId()) {
            case LOADER_LEAVING_ADDRESS_ID:
                try{
                    mDbLeavingAddressId = new JSONObject(data).getString("id");
                } catch(JSONException e){
                    Log.e(TAG, "selectFromAddress.php error converting result: ", e);
                    mDbLeavingAddressId = "0";
                }
                // Post an event, informing that message Id has finished loading
                BusManager
                        .getInstance()
                        .post(new DownloadFinishedEvent(LOADER_LEAVING_ADDRESS_ID));
                break;
            case LOADER_DROPPING_ADDRESS_ID:
                try{
                    mDbDroppingAddressId = new JSONObject(data).getString("id");
                } catch(JSONException e){
                    Log.e(TAG, "selectFromAddress.php error converting result: ", e);
                    mDbDroppingAddressId = "0";
                }
                // Post an event, informing that message Id has finished loading
                BusManager
                        .getInstance()
                        .post(new DownloadFinishedEvent(LOADER_DROPPING_ADDRESS_ID));
                break;
            case LOADER_ROUTE_ID:
                try {
                    String routeId = new JSONObject(data).getString("id");
                    // Do not display a loading when the same result was given
                    if (!mDbRouteId.equals(routeId)) {
                        mDbRouteId = routeId;
                        // Post an event, informing that route ID was loaded
                        BusManager
                                .getInstance()
                                .post(new DownloadFinishedEvent(LOADER_ROUTE_ID));
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "selectFromRoute.php error converting result: ", e);
                    mDbRouteId = "0";
                    mRoute = "";
                    // Post an event, informing that route ID was loaded
                    BusManager
                            .getInstance()
                            .post(new DownloadFinishedEvent(LOADER_ROUTE_ID));
                }
                break;
            case LOADER_PHONE_ID:
                try{
                    mDbPhoneId = String.valueOf(new JSONObject(data).getInt("id"));
                } catch(JSONException e){
                    Log.e(TAG, "selectFromPhone.php error converting result: ", e);
                    mDbPhoneId = "0";
                }
                // Post an event, informing that phone Id has finished loading
                BusManager
                        .getInstance()
                        .post(new DownloadFinishedEvent(LOADER_PHONE_ID));
                break;
            case LOADER_MESSAGE_ID:
                try{
                    mDbMessageId = String.valueOf(new JSONObject(data).getInt("id"));
                } catch(JSONException e){
                    Log.e(TAG, "selectFromMessage.php error converting result: ", e);
                    mDbMessageId = "0";
                }
                // Post an event, informing that message Id has finished loading
                BusManager
                        .getInstance()
                        .post(new DownloadFinishedEvent(LOADER_MESSAGE_ID));
                break;
        }
    }
    @Override
    public void onLoaderReset(android.support.v4.content.Loader<String> loader) {
    }

/*
    //TODO move to background thread and to separate class
	//Task to insert post to DB	
	private class InsertPostTask extends AsyncTask<Void, Void, String> {
				
		protected void onPreExecute () {
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
