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
import lt.justplius.android.pavezikas.facebook.loaders.SelectUserRatingLoader;
import lt.justplius.android.pavezikas.facebook.loaders.UpdateUserGroupsLoader;
import lt.justplius.android.pavezikas.facebook.loaders.UpdateUserInformationLoader;

/**
 * Convenience class containing:
 * - Loader IDs,
 * - singleton instance,
 * - methods for Async Tasks creation.
 */
public class LoadersManager {
    public static final int LOADER_USER_GROUPS = 1;
    public static final int LOADER_USER_ROUTE_PAIRED_GROUPS = 2;
    public static final int LOADER_LEAVING_ADDRESS_ID = 3;
    public static final int LOADER_DROPPING_ADDRESS_ID = 4;
    public static final int LOADER_UPDATE_USER_ROUTE_PAIRED_GROUPS = 5;
    public static final int LOADER_ROUTE_ID = 6;
    public static final int LOADER_PHONE_ID = 7;
    public static final int LOADER_MESSAGE_ID = 8;
    public static final int LOADER_INSERT_POST = 9;
    public static final int LOADER_UPDATE_USER_GROUPS = 10;
    public static final int LOADER_UPDATE_USER_INFORMATION = 11;
    public static final int LOADER_SELECT_USER_RATING = 12;

    private static LoadersManager mLoaderManager = new LoadersManager();
    private static Context mContext;

    public static LoadersManager getInstance(Context context) {
        mContext = context;
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

    public Loader createInsertPostLoader(){
        return new InsertPostLoader(mContext);
    }

    public Loader createUpdateUserGroupsLoader(ArrayList<NameValuePair> pairs){
        return new UpdateUserGroupsLoader(mContext, pairs);
    }

    public Loader createUpdateUserInformationLoader(ArrayList<NameValuePair> pairs){
        return new UpdateUserInformationLoader(mContext, pairs);
    }

    public Loader<String> createSelectUserRatingLoader(){
        return new SelectUserRatingLoader(mContext);
    }
}
