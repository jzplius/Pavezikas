package lt.justplius.android.pavezikas.post;

import android.content.Context;

import lt.justplius.android.pavezikas.post.Post;

/**
 * Created by JUSTPLIUS on 2014.11.13.
 */
public class PostManager {

    private static Post mPost;
    private static PostLoaderManager mLoaderManager;

    private PostManager(){
    }

    public static Post getPost(Context context) {
        if (mPost == null) {
            mPost = new Post(context);
        }
        return mPost;
    }

    public static PostLoaderManager getLoader() {
        if (mLoaderManager == null) {
            mLoaderManager = new PostLoaderManager();
        }
        return mLoaderManager;
    }
}
