package biz.mesto.anaken.githubclient;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.android.volley.Response;
import com.google.android.gms.gcm.GcmListenerService;

public class MyGcmListenerService extends GcmListenerService {

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String repoName = data.getString("repo_name");
        int repoUserId = Integer.parseInt(data.getString("repo_user_id"));

        Repo.get(getApplicationContext(), repoName, repoUserId, new Response.Listener<Repo>() {
            @Override
            public void onResponse(Repo repo) {
                showNotice(repo);
            }
        });
    }

    private void showNotice(Repo repo) {
        Intent resultIntent = new Intent(this, RepoActivity.class);
        resultIntent.putExtra(RepoActivity.EXTRA_REPO, repo);
        resultIntent.putExtra(RepoActivity.EXTRA_NOTICED, 1);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(
            this,
            0,
            resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        );

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder =
            new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notification_icon_transparent)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.notification_icon))
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .setContentTitle("Новый коммит")
                .setContentText("В репозитории " + repo.full_name + " появился новый коммит")
                .setSound(soundUri)
                .setVibrate(new long[] { 1000, 1000, 1000 })
                .setContentIntent(resultPendingIntent);

        int mNotificationId = 1;
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }
}