package lt.justplius.android.pavezikas.facebook;

/**
 * Created by JUSTPLIUS on 2014.11.20.
 */
public class FacebookGroup {
    private String mGroupId;
    private boolean mIsChecked;
    private String mGroupName;

    public FacebookGroup(String groupId, boolean isChecked, String groupName) {
        mGroupId = groupId;
        mIsChecked = isChecked;
        mGroupName = groupName;
    }

    public String getGroupId() {
        return mGroupId;
    }

    public void setGroupId(String groupId) {
        mGroupId = groupId;
    }

    public boolean isChecked() {
        return mIsChecked;
    }

    public void setChecked(boolean isChecked) {
        mIsChecked = isChecked;
    }

    public String getGroupName() {
        return mGroupName;
    }

    public void setGroupName(String groupName) {
        mGroupName = groupName;
    }
}
