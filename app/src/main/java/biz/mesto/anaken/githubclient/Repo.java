package biz.mesto.anaken.githubclient;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.annotations.SerializedName;

public class Repo implements Parcelable {
    @SerializedName("id") public int id;
    @SerializedName("name") public String name;
    @SerializedName("full_name") public String full_name;
    @SerializedName("description") public String description;
    @SerializedName("url") public String url;
    @SerializedName("html_url") public String html_url;
    @SerializedName("updated_at") public String updated_at;
    @SerializedName("contributors_url") public String contributors_url;
    @SerializedName("commits_url") public String commits_url;

    public Repo(Parcel parcel) {
        id = parcel.readInt();
        name = parcel.readString();
        full_name = parcel.readString();
        description = parcel.readString();
        url = parcel.readString();
        html_url = parcel.readString();
        updated_at = parcel.readString();
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
        dest.writeString(name);
        dest.writeString(full_name);
        dest.writeString(description);
        dest.writeString(url);
        dest.writeString(html_url);
        dest.writeString(updated_at);
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
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("repos_subs", new String[]{"name"}, "name = ?", new String[] { full_name }, null, null, null);
        if (c.moveToFirst()) {
            dbHelper.close();
            return 1;
        }
        else {
            dbHelper.close();
            return 0;
        }
    }

    public void setRate(Context context, int rate) {
        DBHelper dbHelper = new DBHelper(context);
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int thisRate = getRate(context);
        cv.put("name", full_name);
        if (rate > 0 && thisRate == 0) {
            db.insert("repos_subs", null, cv);
        }
        else if (rate == 0 && thisRate > 0) {
            db.delete("repos_subs", "name = ?", new String[] { full_name });
        }
        dbHelper.close();
    }

    public void getContributors(final Context context, Response.Listener<User[]> listener) {
        String url = contributors_url;

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        GsonRequest<User[]> jsObjRequest = new GsonRequest<>(
            url,
            User[].class,
            null,
            listener,
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context, "Ошибка запроса к серверу", Toast.LENGTH_SHORT).show();
                }
            }
        );

        requestQueue.add(jsObjRequest);
    }

    public void getCommits(final Context context, Response.Listener<RepoCommit[]> listener) {
        String url = commits_url.replace("{/sha}", "");

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        GsonRequest<RepoCommit[]> jsObjRequest = new GsonRequest<>(
                url,
                RepoCommit[].class,
                null,
                listener,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("MYLOG", "error: " + error.toString());
                        Toast.makeText(context, "Ошибка запроса к серверу", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestQueue.add(jsObjRequest);
    }
}
