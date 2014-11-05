package lt.justplius.android.pavezikas;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import lt.justplius.android.pavezikas.common.HttpPostStringResponse;
import lt.justplius.android.pavezikas.posts.PostListItem;

/**
 * A fragment representing a single Post detail screen.
 * This fragment is either contained in a {@link PostsListActivity}
 * in two-pane mode (on tablets) or a {@link PostDetailActivity}
 * on handsets.
 */
public class PostDetailFragment extends Fragment {
    private TextView mTextViewRouteInfo;
    private TextView mTextViewNameSurname;
    private RatingBar mRatingBar;
    private Button mButtonProfile;
    private TextView mTextViewDate;
    private TextView mTextViewSeatsAvailable;
    private TextView mTextViewTime;
    private TextView mTextViewPrice;
    private TextView mTextViewComment;
    private TextView mTextViewLeavingAddress;
    private TextView mDroppingAddress;
    private ImageButton mImageButtonFacebook;
    private ImageButton mImageButtonCall;
    private ImageButton mImageButtonSms;
    private ImageButton mImageButtonEmail;
    private ProfilePictureView mProfilePictureView;
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_POST_ID = "post_id";
    private ViewStub mViewStub;
    private ProgressBar mProgressBar;

    public static Fragment newInstance (int selectionId){
        Bundle args = new Bundle();
        args.putInt(PostDetailFragment.ARG_POST_ID, selectionId);
        PostDetailFragment fragment = new PostDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PostDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_POST_ID)) {
            // Load the data from server for particular post
            int postId = getArguments().getInt(ARG_POST_ID);
            GetPostDetailsTask getPostDetailsTask = new GetPostDetailsTask();
            getPostDetailsTask.execute(postId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.viewstub, container, false);
        mViewStub = (ViewStub) v.findViewById(R.id.viewStub_import);
        mProgressBar = (ProgressBar) v.findViewById(R.id.viewStub_progressBar);
        return v;
    }

    private class GetPostDetailsTask extends AsyncTask<Integer, Void, String> {
        private static final String TAG = "GetPostDetailsTask";
        private Time leaving_time_from;
        private Time leaving_time_to;
        private String mUrl;

        @Override
        protected void onPreExecute () {
            mUrl = getString(R.string.url_select_post_details);
            leaving_time_from = new Time();
            leaving_time_to = new Time();
        }

        @Override
        protected String doInBackground(Integer... params) {
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("post_id", String.valueOf(params[0])));
            return new HttpPostStringResponse(mUrl, nameValuePairs).returnJSON();
        }

        @Override
        protected void onPostExecute(String result) {
            try{
                // Inflate a default layout when data download is finished
                mProgressBar.setVisibility(View.GONE);
                View v = mViewStub.inflate();
                mTextViewNameSurname = (TextView) v.findViewById(R.id.post_details_name_surname);
                mRatingBar = (RatingBar) v.findViewById(R.id.post_details_rating);
                mButtonProfile = (Button) v.findViewById(R.id.post_details_profile);
                mTextViewDate = (TextView) v.findViewById(R.id.post_details_date);
                mTextViewSeatsAvailable = (TextView) v.findViewById(R.id.post_details_seats);
                mTextViewTime = (TextView) v.findViewById(R.id.post_details_time);
                mTextViewPrice = (TextView) v.findViewById(R.id.post_details_price);
                mTextViewComment = (TextView) v.findViewById(R.id.post_details_comment);
                mTextViewLeavingAddress = (TextView) v.findViewById(R.id.post_details_leaving_address);
                mDroppingAddress = (TextView) v.findViewById(R.id.post_details_dropping_address);
                mImageButtonFacebook = (ImageButton) v.findViewById(R.id.post_details_button_facebook);
                mImageButtonCall = (ImageButton) v.findViewById(R.id.post_details_button_call);
                mImageButtonSms = (ImageButton) v.findViewById(R.id.post_details_button_sms);
                mImageButtonEmail = (ImageButton) v.findViewById(R.id.post_details_button_email);
                mProfilePictureView = (ProfilePictureView) v.findViewById(R.id.post_details_profile_picture);
                mTextViewRouteInfo = (TextView) v.findViewById(R.id.post_details_route_info);

                // Set put downloaded data on to views
                JSONObject jsonObject = new JSONObject(result);
                mTextViewRouteInfo.setText(jsonObject.getString("route"));
                mTextViewNameSurname.setText(jsonObject.getString("name_surname"));
                mProfilePictureView.setProfileId(jsonObject.getString("user_id"));
                if (jsonObject.getInt("ratings_count") > 0) {
                    mRatingBar.setRating((float) jsonObject.getDouble("rating"));
                }
                mButtonProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
                setLeavingTimeFrom(jsonObject.getString("leaving_time_from"));
                setLeavingTimeTo(jsonObject.getString("leaving_time_to"));
                mTextViewDate.setText(
                        PostListItem.getFormattedDate(
                                getActivity(), jsonObject.getString("leaving_date")));
                mTextViewSeatsAvailable.setText(
                        PostListItem.getFormattedSeats(
                                getActivity(), jsonObject.getString("seats_available")));
                mTextViewTime.setText(getTime());
                mTextViewPrice.setText(
                        PostListItem.getFormattedPrice(
                                getActivity(), jsonObject.getString("price")));
                mTextViewLeavingAddress.setText(jsonObject.getString("leaving_address"));
                mDroppingAddress.setText(jsonObject.getString("dropping_address"));

                //dynamically remove message view from layout if such does not exist
                if (jsonObject.getString("message").equals("null")){
                    mTextViewComment.setVisibility(View.GONE);
                } else {
                    mTextViewComment.setText(jsonObject.getString("message"));
                }
            }catch(JSONException e){
                Log.e(TAG, "Error parsing post information from JSON: ", e);
            } catch (ParseException e) {
                Log.e(TAG, "Error parsing date: ", e);
            }
        }

        //get posts time
        public String getTime(){
            StringBuilder timeString = new StringBuilder();
            timeString.append(leaving_time_from.hour)
                    .append(":");
            if (leaving_time_from.minute < 10){
                timeString.append("0");
            }
            timeString.append(leaving_time_from.minute)
                    .append(" - ");
            timeString.append(leaving_time_to.hour)
                    .append(":");
            if (leaving_time_to.minute < 10){
                timeString.append("0");
            }
            timeString.append(leaving_time_to.minute);
            return timeString.toString();
        }

        public void setLeavingTimeFrom(String _time) throws ParseException{
            leaving_time_from.set(new SimpleDateFormat("HH:mm:ss").parse(_time).getTime());
        }

        public void setLeavingTimeTo(String _time) throws ParseException{
            leaving_time_to.set(new SimpleDateFormat("HH:mm:ss").parse(_time).getTime());
        }
    }
}
