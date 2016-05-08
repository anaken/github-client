package biz.mesto.anaken.githubclient;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import java.util.ArrayList;

public class Repo implements Parcelable {
    @SerializedName("id") public int id;
    @SerializedName("user_id") public int user_id;
    @SerializedName("name") public String name;
    @SerializedName("full_name") public String full_name;
    @SerializedName("description") public String description;
    @SerializedName("url") public String url;
    @SerializedName("html_url") public String html_url;
    @SerializedName("updated_at") public String updated_at;
    @SerializedName("pushed_at") public String pushed_at;
    @SerializedName("contributors_url") public String contributors_url;
    @SerializedName("commits_url") public String commits_url;

    public Repo(Parcel parcel) {
        id = parcel.readInt();
        user_id = parcel.readInt();
        name = parcel.readString();
        full_name = parcel.readString();
        description = parcel.readString();
        url = parcel.readString();
        html_url = parcel.readString();
        updated_at = parcel.readString();
        pushed_at = parcel.readString();
        contributors_url = parcel.readString();
        commits_url = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(user_id);
        dest.writeString(name);
        dest.writeString(full_name);
        dest.writeString(description);
        dest.writeString(url);
        dest.writeString(html_url);
        dest.writeString(updated_at);
        dest.writeString(pushed_at);
        dest.writeString(contributors_url);
        dest.writeString(commits_url);
    }

    public static Creator<Repo> CREATOR = new Creator<Repo>() {

        @Override
        public Repo createFromParcel(Parcel source) {
            return new Repo(source);
        }

        @Override
        public Repo[] newArray(int size) {
            return new Repo[size];
        }

    };

    public int getRate(Context context) {
        SQLiteDatabase db = Helper.db(context);
        Cursor c = db.query("repos_subs", new String[]{"name"}, "name = ?", new String[] { full_name }, null, null, null);
        int result = c.moveToFirst() ? 1 : 0;
        db.close();
        return result;
    }

    public void setRate(Context context, int rate, String ts) {
        SQLiteDatabase db = Helper.db(context);
        int thisRate = getRate(context);
        if (rate > 0) {
            ContentValues cv = new ContentValues();
            if (ts == null) {
                ts = pushed_at;
            }
            DateTime dt = DateTime.parse(ts);
            dt = dt.plusSeconds(1);
            ts = dt.toString();
            cv.put("ts", ts);
            if (thisRate > 0) {
                db.update("repos_subs", cv, "name = ?", new String[] { full_name });
            }
            else {
                cv.put("name", full_name);
                db.insert("repos_subs", null, cv);
            }
        }
        else if (rate == 0 && thisRate > 0) {
            db.delete("repos_subs", "name = ?", new String[] { full_name });
        }
        db.close();
    }

    public void store(Context context) {
        final Parcel parcel = Parcel.obtain();
        writeToParcel(parcel, 0);
        byte[] data = parcel.marshall();
        parcel.recycle();

        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("user_id", user_id);
        values.put("full_name", full_name);
        values.put("data", data);
        SQLiteDatabase db = Helper.db(context);
        db.insert("repos", null, values);
        db.close();
    }

    public static void get(final Context context, String full_name, final int user_id, final Response.Listener<Repo> listener) {
        if (Helper.isOnline(context)) {
            String url = "https://api.github.com/repos/" + full_name;

            Response.Listener<Repo> repoListener = new Response.Listener<Repo>() {
                @Override
                public void onResponse(Repo response) {
                    SQLiteDatabase db = Helper.db(context);
                    db.delete("repos", "id = ?", new String[]{ Integer.toString(response.id) });
                    db.close();
                    response.user_id = user_id;
                    response.store(context);
                    listener.onResponse(response);
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(context);
            GsonRequest<Repo> jsObjRequest = new GsonRequest<>(
                    url,
                    Repo.class,
                    null,
                    repoListener,
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(context, "Ошибка запроса к серверу", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
            requestQueue.add(jsObjRequest);
        }
        else {
            SQLiteDatabase db = Helper.db(context);
            Cursor c = db.query("repos", null, "full_name = ?", new String[]{ full_name }, null, null, null);
            if (c.moveToFirst()) {
                byte[] data = c.getBlob(c.getColumnIndex("data"));
                final Parcel parcel = Parcel.obtain();
                parcel.unmarshall(data, 0, data.length);
                parcel.setDataPosition(0);
                Repo repo = new Repo(parcel);
                parcel.recycle();
                listener.onResponse(repo);
            }
            else {
                listener.onResponse(null);
            }
            db.close();
        }
    }

    public void getContributors(final Context context, final Response.Listener<User[]> listener) {
        if (Helper.isOnline(context)) {
            Response.Listener<User[]> resultListener = new Response.Listener<User[]>() {
                @Override
                public void onResponse(User[] response) {
                    SQLiteDatabase db = Helper.db(context);
                    db.delete("repos_contribs", "repo_id = ?", new String[]{ Integer.toString(id) });
                    db.close();
                    int sort = 0;
                    for (User u : response) {
                        u.contrib_id = id;
                        u.storeAsContrib(context, sort);
                        sort++;
                    }
                    listener.onResponse(response);
                }
            };

            String url = contributors_url;
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            GsonRequest<User[]> jsObjRequest = new GsonRequest<>(
                url,
                User[].class,
                null,
                resultListener,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Ошибка запроса к серверу", Toast.LENGTH_SHORT).show();
                    }
                }
            );
            requestQueue.add(jsObjRequest);
        }
        else {
            SQLiteDatabase db = Helper.db(context);
            Cursor c = db.query("repos_contribs", null, "repo_id = ?", new String[]{ Integer.toString(id) }, null, null, "sort");
            ArrayList<User> users = new ArrayList<>();
            if (c.moveToFirst()) {
                do {
                    byte[] data = c.getBlob(c.getColumnIndex("data"));
                    final Parcel parcel = Parcel.obtain();
                    parcel.unmarshall(data, 0, data.length);
                    parcel.setDataPosition(0);
                    User u = new User(parcel);
                    parcel.recycle();
                    users.add(u);
                } while (c.moveToNext());
            }
            db.close();

            User[] response = new User[users.size()];
            response = users.toArray(response);
            listener.onResponse(response);
        }
    }

    public void getCommits(final Context context, final Response.Listener<RepoCommit[]> listener) {
        getCommits(context, listener, null);
    }

    public void getCommits(final Context context, final Response.Listener<RepoCommit[]> listener, String since) {
        if (Helper.isOnline(context)) {
            Response.Listener<RepoCommit[]> resultListener = new Response.Listener<RepoCommit[]>() {
                @Override
                public void onResponse(RepoCommit[] response) {
                    SQLiteDatabase db = Helper.db(context);
                    db.delete("repos_commits", "repo_id = ?", new String[]{ Integer.toString(id) });
                    db.close();
                    for (RepoCommit rc : response) {
                        rc.repo_id = id;
                        rc.store(context);
                    }
                    listener.onResponse(response);
                }
            };

            String url = commits_url.replace("{/sha}", "");
            if (since != null) {
                url += "?since=" + since;
            }

            Log.i("MYLOG", "checking commits: " + url);

            RequestQueue requestQueue = Volley.newRequestQueue(context);
            GsonRequest<RepoCommit[]> jsObjRequest = new GsonRequest<>(
                url,
                RepoCommit[].class,
                null,
                resultListener,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Ошибка запроса к серверу", Toast.LENGTH_SHORT).show();
                    }
                }
            );
            requestQueue.add(jsObjRequest);
        }
        else {
            String where = "repo_id = ?";
            String[] whereParams;
            if (since != null) {
                where += " and date > ?";
                whereParams = new String[]{ Integer.toString(id), since };
            }
            else {
                whereParams = new String[]{ Integer.toString(id) };
            }
            SQLiteDatabase db = Helper.db(context);
            Cursor c = db.query("repos_commits", null, where, whereParams, null, null, "date");
            ArrayList<RepoCommit> commits = new ArrayList<>();
            if (c.moveToFirst()) {
                do {
                    byte[] data = c.getBlob(c.getColumnIndex("data"));
                    final Parcel parcel = Parcel.obtain();
                    parcel.unmarshall(data, 0, data.length);
                    parcel.setDataPosition(0);
                    RepoCommit rc = new RepoCommit(parcel);
                    parcel.recycle();
                    commits.add(rc);
                } while (c.moveToNext());
            }
            db.close();

            RepoCommit[] response = new RepoCommit[commits.size()];
            response = commits.toArray(response);
            listener.onResponse(response);
        }
    }
}
