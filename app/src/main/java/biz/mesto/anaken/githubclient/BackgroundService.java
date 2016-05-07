package biz.mesto.anaken.githubclient;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.Response;

import java.util.concurrent.TimeUnit;

public class BackgroundService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        
        checkNewCommits();

        return START_STICKY;
    }

    private void checkNewCommits() {
        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        if ( ! Helper.isOnline(getApplicationContext())) {
                            continue;
                        }

                        SQLiteDatabase db = Helper.db(getApplicationContext());
                        Cursor c = db.query("repos_subs", new String[]{"name", "user_id", "ts"}, null, null, null, null, null);
                        if (c.moveToFirst()) {
                            do {
                                final String repoName = c.getString(0);
                                int repoUserId = c.getInt(1);
                                final String repoTS = c.getString(2);
                                Repo.get(getApplicationContext(), repoName, repoUserId, new Response.Listener<Repo>() {
                                    @Override
                                    public void onResponse(final Repo repo) {
                                        repo.getCommits(getApplicationContext(), new Response.Listener<RepoCommit[]>() {
                                            @Override
                                            public void onResponse(RepoCommit[] response) {
                                                if (response.length > 0) {
                                                    showNotice(repo);
                                                    repo.setRate(getApplicationContext(), 1, repo.pushed_at);
                                                }
                                            }
                                        }, repoTS);
                                    }
                                });
                            } while (c.moveToNext());
                        }
                        db.close();

                        TimeUnit.SECONDS.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void showNotice(Repo repo) {
        Intent resultIntent = new Intent(this, RepoActivity.class);
        resultIntent.putExtra(RepoActivity.EXTRA_REPO, repo);
        resultIntent.putExtra(RepoActivity.EXTRA_NOTICED, true);
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
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                .setContentIntent(resultPendingIntent);

        int mNotificationId = 1;
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }
}
