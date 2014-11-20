package lt.justplius.android.pavezikas.post;

import android.content.Context;

import lt.justplius.android.pavezikas.post.Post;

/**
 * Created by JUSTPLIUS on 2014.11.13.
 */
public class PostManager {

    private static Post mPost;

    private PostManager(){
    }

    public static Post getInstance(Context context) {
        if (mPost == null) {
            mPost = new Post(context);
        }
        return mPost;
    }
}
