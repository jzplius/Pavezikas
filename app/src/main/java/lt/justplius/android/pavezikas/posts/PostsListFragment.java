package lt.justplius.android.pavezikas.posts;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ListView;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;

import lt.justplius.android.pavezikas.R;
import lt.justplius.android.pavezikas.add_post.AddPostActivity;
import lt.justplius.android.pavezikas.common.HttpPostStringResponse;

/**
 * A list fragment representing a list of Posts. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link PostDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class PostsListFragment extends ListFragment {
    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    private static final String NVP_POSTS_FILTER = "posts_filter";

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = null;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    private ArrayList<PostListItem> mItems;
    private PostsListViewAdapter mAdapter;
    private String mFilter;
    private MenuItem mMenuItemFilter;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(int id);
    }

    // Enum for choosing filter
    private enum Filter {
        FILTER_NONE("none"), FILTER_DRIVERS("drivers"), FILTER_PASSENGERS("passengers");

        private final String mType;

        Filter(String type) {
            mType = type;
        }

        @Override
        public String toString() {
            return mType;
        }
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PostsListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);

        mItems = new ArrayList<>();
        mAdapter = new PostsListViewAdapter(getActivity(), mItems);
        downloadPosts(Filter.FILTER_NONE);

        // Configure fragment-specific action bar
        ActionBar actionBar = getActivity().getActionBar();
        if (actionBar != null) {
            View view = actionBar.getCustomView();

            if (view != null) {
                // OnClick show menu
                ImageButton imageButtonMenu = (ImageButton) view.findViewById(R.id.action_bar_main_menu);
                imageButtonMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((PostsListActivity) getActivity()).getSlidingMenu().showMenu();
                    }
                });

                // OnClick reload posts
                final ImageButton imageButtonRefresh = (ImageButton) view.findViewById(R.id.action_bar_refresh);
                imageButtonRefresh.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO update view
                        downloadPosts(Filter.FILTER_NONE);
                        // Animate rotation for 1 second
                        RotateAnimation rotateAnimation = new RotateAnimation(
                                0,
                                359,
                                Animation.RELATIVE_TO_SELF,
                                0.5f,
                                Animation.RELATIVE_TO_SELF,
                                0.5f);
                        rotateAnimation.setRepeatMode(Animation.RESTART);
                        rotateAnimation.setDuration(500);
                        imageButtonRefresh.startAnimation(rotateAnimation);
                    }
                });
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }


    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        if (mCallbacks != null) {
            mCallbacks.onItemSelected(mItems.get(position).getId());
        }
    }


    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.posts_list_menu, menu);

        // Set selected filter type title
        mMenuItemFilter = menu.getItem(1);
        mMenuItemFilter.setTitle(
                getString(
                        R.string.filter_type,
                        getString(R.string.all)));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.add:
                Intent intent = new Intent(getActivity(), AddPostActivity.class);
                startActivity(intent);
                break;
            case R.id.search:
                break;
            case R.id.drivers:
                downloadPosts(Filter.FILTER_DRIVERS);
                mMenuItemFilter.setTitle(getString(
                        R.string.filter_type,
                        getString(R.string.drivers)));
                break;
            case R.id.passengers:
                downloadPosts(Filter.FILTER_PASSENGERS);
                mMenuItemFilter.setTitle(getString(
                        R.string.filter_type,
                        getString(R.string.passengers)));
                break;
            case R.id.all:
                downloadPosts(Filter.FILTER_NONE);
                mMenuItemFilter.setTitle(getString(
                        R.string.filter_type,
                        getString(R.string.all)));
                break;
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    private void downloadPosts(Filter filter) {
        new GetPostsTask().execute(new BasicNameValuePair(NVP_POSTS_FILTER, filter.toString()));
    }

    private class GetPostsTask extends AsyncTask<NameValuePair, Void, String> {
        private static final String TAG = "GetPostsTask";
        private String mUrl;
        private ArrayList<NameValuePair> mPairs;

        protected void onPreExecute () {
            mUrl = getString(R.string.url_select_posts);
            mPairs = new ArrayList<>();
        }

        @Override
        protected String doInBackground(NameValuePair... params) {
            Collections.addAll(mPairs, params);
            return new HttpPostStringResponse(mUrl, mPairs).returnJSON();
        }

        protected void onPostExecute(String result) {
            try{
                JSONArray jsonArray = new JSONArray(result);
                JSONObject jsonObject;
                PostListItem post;
                mItems.clear();

                for(int i=0;i<jsonArray.length();i++){
                    jsonObject = jsonArray.getJSONObject(i);
                    post = new PostListItem();
                    post.setId(jsonObject.getInt("post_id"));
                    post.setName(jsonObject.getString("name_surname"));
                    post.setRouteInformation(jsonObject.getString("route"));
                    post.setSeatsAvailable(jsonObject.getInt("seats_available"));
                    post.setDate(jsonObject.getString("leaving_date"));
                    post.setLeavingTimeFrom(jsonObject.getString("leaving_time_from"));
                    post.setLeavingTimeTo(jsonObject.getString("leaving_time_to"));
                    post.setUserId(jsonObject.getString("user_id"));
                    post.setPostType(jsonObject.getString("post_type"));
                    post.setPrice(jsonObject.getString("price"));

                    /*if (jsonObject.getInt("ratings_count") > 0){
                        //TODO get current rating
                        i++;
                    }*/
                    mItems.add(post);
                }

                if (getListAdapter() == null) {
                    setListAdapter(mAdapter);
                }
                mAdapter.notifyDataSetChanged();

                // Perform first posts list item click, so that it would be selected by default
                if (((PostsListActivity) getActivity()).isTwoPane()) {

                    ListView listView = getListView();
                    listView.performItemClick(
                            listView.getAdapter().getView(0, null, null),
                            0,
                            listView.getAdapter().getItemId(0));
                }

            } catch(JSONException | ParseException e){
                Log.e(TAG, "Get posts async task error: ", e);
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = null;
    }
}
