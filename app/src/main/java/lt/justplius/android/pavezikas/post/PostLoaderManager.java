package lt.justplius.android.pavezikas.post;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;

import org.apache.http.NameValuePair;

import java.util.ArrayList;

import lt.justplius.android.pavezikas.add_post.loaders.AddressIdLoader;
import lt.justplius.android.pavezikas.add_post.loaders.RouteIdLoader;
import lt.justplius.android.pavezikas.add_post.loaders.UpdateUserRoutePairedGroupsLoader;
import lt.justplius.android.pavezikas.add_post.loaders.UserGroupsLoader;
import lt.justplius.android.pavezikas.add_post.loaders.UserRoutePairedGroupsLoader;

/**
 * Created by JUSTPLIUS on 2014.12.10.
 */
public class PostLoaderManager {
    public static final int LOADER_USER_GROUPS = 0;
    public static final int LOADER_USER_ROUTE_PAIRED_GROUPS = 1;
    public static final int LOADER_LEAVING_ADDRESS_ID = 2;
    public static final int LOADER_DROPPING_ADDRESS_ID = 3;
    public static final int LOADER_UPDATE_USER_ROUTE_PAIRED_GROUPS = 4;
    public static final int LOADER_ROUTE_ID = 5;

    public static LoaderManager getLoaderManager(Context context) {
        return ((Activity) context).getLoaderManager();
    }

    public Loader<String> createUserGroupsLoader(Context context){
        return new UserGroupsLoader(context);
    }

    public Loader<String> createUserRoutePairedGroupsLoader(Context context){
        return new UserRoutePairedGroupsLoader(context);
    }

    public Loader createUpdateUserRoutePairedGroupsLoader(
            Context context, ArrayList<NameValuePair> pairs){
        return new UpdateUserRoutePairedGroupsLoader(context, pairs);
    }

    public Loader<String> createAddressIdLoader(Context context, String address){
        return new AddressIdLoader(context, address);
    }

    public Loader<String> createRouteIdLoader(Context context, String address){
        return new RouteIdLoader(context, address);
    }
}
