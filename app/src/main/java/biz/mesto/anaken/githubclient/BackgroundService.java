package biz.mesto.anaken.githubclient;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.android.volley.Response;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;
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

                        TimeUnit.SECONDS.sleep(20);

                        SQLiteDatabase db = Helper.db(getApplicationContext());
                        Cursor c = db.query("repos_subs", new String[]{"name", "user_id", "ts"}, null, null, null, null, null);
                        if (c.moveToFirst()) {
                            do {
                                final String repoName = c.getString(0);
                                int repoUserId = c.getInt(1);
                                final String repoTS = c.getString(2);
                                //final String repoTS = "2010-06-19T21:18:37Z";
                                Repo.get(getApplicationContext(), repoName, repoUserId, new Response.Listener<Repo>() {
                                    @Override
                                    public void onResponse(final Repo repo) {
                                        repo.getCommits(getApplicationContext(), new Response.Listener<RepoCommit[]>() {
                                            @Override
                                            public void onResponse(RepoCommit[] response) {
                                                if (response.length > 0) {
                                                    sendNotice(repo);
                                                    repo.setRate(getApplicationContext(), 1, repo.pushed_at);
                                                }
                                            }
                                        }, repoTS);
                                    }
                                });
                            } while (c.moveToNext());
                        }
                        db.close();

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

    private void sendNotice(Repo repo) {
        Map<String, String> args = new HashMap<>();
        args.put("message", "В репозитории " + repo.full_name + " появился новый коммит");
        args.put("repo_name", repo.full_name);
        args.put("repo_user_id", Integer.toString(repo.user_id));
        try {
            GcmSender.send(args);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
