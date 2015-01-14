package lt.justplius.android.pavezikas.add_post.events;

import lt.justplius.android.pavezikas.mangers.DownloadsManager;

/**
 * Event, informing, that download has finished.
 * Adds last download to static member, indicating AsyncTaskLoader ID.
 */
public class DownloadFinishedEvent {
    public static int sLoaderId;

    public DownloadFinishedEvent (int loaderId) {
        sLoaderId = loaderId;
        DownloadsManager.removeFromDownloadsList(loaderId);
    }
}
