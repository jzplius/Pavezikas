package lt.justplius.android.pavezikas.add_post.events;

import lt.justplius.android.pavezikas.mangers.DownloadsManager;

/**
 * Event, informing, that download has started.
 * Constructor adds AsyncTaskLoader ID to downloads list.
 */
public class DownloadStartedEvent {
    public static int sLoaderId;

    public DownloadStartedEvent(int loaderId) {
        sLoaderId = loaderId;
        DownloadsManager.addToDownloadsList(loaderId);
    }
}
