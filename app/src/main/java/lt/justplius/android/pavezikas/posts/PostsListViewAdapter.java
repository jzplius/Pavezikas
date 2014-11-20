package lt.justplius.android.pavezikas.posts;

import java.text.ParseException;
import java.util.ArrayList;

import com.facebook.widget.ProfilePictureView;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import lt.justplius.android.pavezikas.R;

public class PostsListViewAdapter extends ArrayAdapter<PostListItem> {
    private static final String TAG = "PostsListViewAdapter";
    private ArrayList<PostListItem> mItems;
        
        public PostsListViewAdapter(Context context, ArrayList<PostListItem> items) {
            super(context, R.layout.activity_post_list);
            mItems = items;
        }
        
        @Override
        public int getCount() {
            return mItems.size();
        }

        /**
         * {@inheritDoc}
         *
         * @param position
         */
        @Override
        public PostListItem getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // View holder for single ListView item
            final ViewHolder holder;

            // First time initialize ViewHolder, other times retrieve saved one
            if (convertView == null) {
                // Inflate a "list_view_item_currency.xml" layout into ListView's item
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.posts_list_item, parent, false);
                holder = new ViewHolder();

                holder.routeInformation = (TextView) convertView.findViewById(R.id.post_list_item_text_route_info);
                holder.profilePicture = (ProfilePictureView) convertView.findViewById(R.id.post_list_item_profile_pic);
                holder.seatsAvailable = (TextView) convertView.findViewById(R.id.post_list_item_seats_available);
                holder.dateInformation = (TextView) convertView.findViewById(R.id.post_list_item_date_information);
                holder.timeInformation = (TextView) convertView.findViewById(R.id.post_list_item_time_information);
                holder.price = (TextView) convertView.findViewById(R.id.post_list_item_price);
                holder.postTypeIcon = (ImageView) convertView.findViewById(R.id.post_list_item_imageView_post_type);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.routeInformation.setText(mItems.get(position).getRouteInformation());
            holder.profilePicture.setProfileId(String.valueOf(mItems.get(position).getUserId()));
            holder.seatsAvailable.setText(mItems.get(position).getSeatsAvailable());
            try {
                holder.dateInformation.setText(mItems.get(position).getDate(getContext()));
            } catch (ParseException e) {
                Log.e(TAG, "Error parsing date", e);
            }
            holder.timeInformation.setText(mItems.get(position).getTime());
            holder.price.setText(mItems.get(position).getPrice());
            // Determine whether post type is driver ('d'), o passenger ('p')
            if (mItems.get(position).getPostType().equals("d")) {
                holder.postTypeIcon.setImageDrawable(
                        getContext().getResources().getDrawable(R.drawable.icon_type_driver));
            } else {
                holder.postTypeIcon.setImageDrawable(
                        getContext().getResources().getDrawable(R.drawable.icon_type_passenger));
            }

            return convertView;
        }
        
        //static views for each row
        static class ViewHolder {        
            TextView routeInformation;
            ProfilePictureView profilePicture;
            TextView seatsAvailable;
            TextView dateInformation;
            TextView timeInformation;
            TextView price;
            ImageView postTypeIcon;
        }             
}