package lt.justplius.android.pavezikas.mangers;

import java.util.ArrayList;

/**
 * Contains list of currently being loaded AsyncTasks and lets check if
 * particular one is currently being downloaded.
 */
public class DownloadsManager {
    // TODO make private
    public static ArrayList<Integer> sDownloads = new ArrayList<>();

    private DownloadsManager(){}

    public static void addToDownloadsList(int loaderId) {
        if (!sDownloads.contains(loaderId)) {
            sDownloads.add(loaderId);
        }
    }

    public static void removeFromDownloadsList(int loaderId) {
        if (sDownloads.contains(loaderId)) {
            sDownloads.remove(sDownloads.indexOf(loaderId));
        }
    }

    public static boolean isDownloading() {
        return !sDownloads.isEmpty();
    }

    public static boolean isBeingDownloaded(int loaderId){
        return sDownloads.contains(loaderId);
    }
}
