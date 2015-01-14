package lt.justplius.android.pavezikas.mangers;

import android.content.Context;
import android.support.v4.content.Loader;

import org.apache.http.NameValuePair;

import java.util.ArrayList;

import lt.justplius.android.pavezikas.add_post.loaders.AddressIdLoader;
import lt.justplius.android.pavezikas.add_post.loaders.InsertPostLoader;
import lt.justplius.android.pavezikas.add_post.loaders.MessageIdLoader;
import lt.justplius.android.pavezikas.add_post.loaders.PhoneIdLoader;
import lt.justplius.android.pavezikas.add_post.loaders.RouteIdLoader;
import lt.justplius.android.pavezikas.add_post.loaders.UpdateUserRoutePairedGroupsLoader;
import lt.justplius.android.pavezikas.add_post.loaders.UserGroupsLoader;
import lt.justplius.android.pavezikas.add_post.loaders.UserRoutePairedGroupsLoader;

/**
 * Convenience class containing:
 * - Loader IDs,
 * - singleton instance,
 * - methods for Async Tasks creation.
 */
public class PostLoadersManager {
    public static final int LOADER_USER_GROUPS = 1;
    public static final int LOADER_USER_ROUTE_PAIRED_GROUPS = 2;
    public static final int LOADER_LEAVING_ADDRESS_ID = 3;
    public static final int LOADER_DROPPING_ADDRESS_ID = 4;
    public static final int LOADER_UPDATE_USER_ROUTE_PAIRED_GROUPS = 5;
    public static final int LOADER_ROUTE_ID = 6;
    public static final int LOADER_PHONE_ID = 7;
    public static final int LOADER_MESSAGE_ID = 8;
    public static final int LOADER_INSERT_POST = 9;

    private static PostLoadersManager mLoaderManager;
    private static Context mContext;

    public static PostLoadersManager getInstance(Context context) {
        mContext = context;

        if (mLoaderManager == null) {
            mLoaderManager = new PostLoadersManager();
        }
        return mLoaderManager;
    }

    public Loader<String> createUserGroupsLoader(){
        return new UserGroupsLoader(mContext);
    }

    public Loader<String> createUserRoutePairedGroupsLoader(){
        return new UserRoutePairedGroupsLoader(mContext);
    }

    public Loader createUpdateUserRoutePairedGroupsLoader(ArrayList<NameValuePair> pairs){
        return new UpdateUserRoutePairedGroupsLoader(mContext, pairs);
    }

    public Loader<String> createAddressIdLoader(String address){
        return new AddressIdLoader(mContext, address);
    }

    public Loader<String> createRouteIdLoader(){
        return new RouteIdLoader(mContext);
    }

    public Loader<String> createPhoneIdLoader(String phone){
        return new PhoneIdLoader(mContext, phone);
    }

    public Loader<String> createMessageIdLoader(String phone){
        return new MessageIdLoader(mContext, phone);
    }

    public Loader<String> createInsertPostLoader(){
        return new InsertPostLoader(mContext);
    }
}
