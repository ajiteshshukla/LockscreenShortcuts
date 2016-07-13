package com.acubeapps.lockscreen.shortcuts.ad;

import com.acubeapps.lockscreen.shortcuts.Injectors;
import com.acubeapps.lockscreen.shortcuts.video.VideoStore;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import javax.inject.Inject;

/**
 * Created by anshul.srivastava on 06/07/16.
 */
public class DownloadReceiver extends BroadcastReceiver {

    @Inject
    public VideoStore videoStore;

    public DownloadReceiver() {
        Injectors.appComponent().injectDownloadReciever(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
            DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            long downloadId = intent.getLongExtra(
                    DownloadManager.EXTRA_DOWNLOAD_ID, 0);
            String videoId = videoStore.getVideoId(downloadId);
            if (videoId != null) {
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(downloadId);
                Cursor c = dm.query(query);
                if (c.moveToFirst()) {
                    int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                        String uri = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                        videoStore.setDownloadedPreviewUri(videoId, uri);
                        videoStore.apply();
                    }
                }
            }
        }
    }
}
