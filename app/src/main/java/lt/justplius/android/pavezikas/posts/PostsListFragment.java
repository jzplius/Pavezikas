package lt.justplius.android.pavezikas.posts;

import android.app.Activity;
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
import android.widget.ListView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;

import lt.justplius.android.pavezikas.PostsListActivity;
import lt.justplius.android.pavezikas.R;
import lt.justplius.android.pavezikas.common.HttpPostStringResponse;

/**
 * A list fragment representing a list of Posts. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link lt.justplius.android.pavezikas.PostDetailFragment}.
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

        GetPostsTask getPostsTask = new GetPostsTask();
        getPostsTask.execute();
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String toastText = null;
        switch (item.getItemId()) {
            case R.id.add:
                toastText = "Paspaudei 'add'";
                break;
            case R.id.search:
                toastText = "Paspaudei 'search'";
                break;
            case R.id.drivers:
                toastText = "Paspaudei 'drivers'";
                break;
            case R.id.passengers:
                toastText = "Paspaud�te 'Passengers'";
                break;
            case R.id.all:
                toastText = "Paspaud�te 'All'";
                break;
        }
        if (toastText != null) {
            Toast.makeText(getActivity(), toastText, Toast.LENGTH_LONG).show();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    private class GetPostsTask extends AsyncTask<Void, Void, String> {
        private static final String TAG = "GetPostsTask";
        private String mUrl;

        protected void onPreExecute () {
            mUrl = getString(R.string.url_get_posts);
        }

        @Override
        protected String doInBackground(Void... params) {
            return new HttpPostStringResponse(mUrl, null).returnJSON();
        }

        protected void onPostExecute(String result) {
            try{
                JSONArray jsonArray = new JSONArray(result);
                JSONObject jsonObject;
                PostListItem post;
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

                    /*if (jsonObject.getInt("ratings_count") > 0){
                        //TODO get current rating
                        i++;
                    }*/
                    mItems.add(post);
                }
                // Perform first posts list item click, so that it would be selected by default
                if (getListAdapter() == null && ((PostsListActivity) getActivity()).isTwoPane()) {
                    setListAdapter(mAdapter);
                    ListView listView = getListView();
                    listView.performItemClick(
                            listView.getAdapter().getView(0, null, null),
                            0,
                            listView.getAdapter().getItemId(0));
                } else {
                    setListAdapter(mAdapter);
                }

                mAdapter.notifyDataSetChanged();

            }catch(JSONException e){
                Log.e(TAG, "Get posts async task error: ", e);
            } catch (ParseException e) {
                e.printStackTrace();
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
