package biz.mesto.anaken.githubclient;

import android.app.DownloadManager;
import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class DownloadService extends IntentService {

    public static final String CHANNEL = BackgroundService.class.getSimpleName()+".broadcast";

    public DownloadService() {
        super("DownloadService");
    }

    public static void startActionDownload(Context context, String url, String localName) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra("url", url);
        intent.putExtra("name", localName);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String url = intent.getStringExtra("url");
            final String name = intent.getStringExtra("name");
            handleActionDownload(url, name);
        }
    }

    private void handleActionDownload(String url, String localName) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

        request.setDescription("Commit archive");
        request.setTitle("Download");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, localName);

        final DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        final long downloadId = manager.enqueue(request);

        boolean downloading = true;

        while (downloading) {

            DownloadManager.Query q = new DownloadManager.Query();
            q.setFilterById(downloadId);

            Cursor cursor = manager.query(q);
            cursor.moveToFirst();
            int bytes_downloaded = cursor.getInt(cursor
                    .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
            int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

            if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                downloading = false;
            }

            final int dl_progress = (bytes_downloaded * 100) / bytes_total;

            Log.e("MYLOG", "downloading... " + dl_progress);

            cursor.close();

        }
    }

    private void sendResult() {
        Intent intent = new Intent(CHANNEL);
        sendBroadcast(intent);
    }
}
