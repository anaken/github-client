package biz.mesto.anaken.githubclient;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.annotations.SerializedName;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class User implements Parcelable {

    final private static String URL_USERS = "https://api.github.com/users";

    @SerializedName("login") public String login;
    @SerializedName("id") public int id;
    @SerializedName("url") public String url;
    @SerializedName("html_url") public String html_url;
    @SerializedName("repos_url") public String repos_url;
    @SerializedName("avatar_url") public String avatar_url;
    @SerializedName("contrib_id") public int contrib_id;

    public User(Parcel parcel) {
        login = parcel.readString();
        id = parcel.readInt();
        url = parcel.readString();
        html_url = parcel.readString();
        repos_url = parcel.readString();
        avatar_url = parcel.readString();
        contrib_id = parcel.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(login);
        dest.writeInt(id);
        dest.writeString(url);
        dest.writeString(html_url);
        dest.writeString(repos_url);
        dest.writeString(avatar_url);
        dest.writeInt(contrib_id);
    }

    public static Creator<User> CREATOR = new Creator<User>() {

        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }

    };

    public static void getUsers(final Context context, final Response.Listener<User[]> listener, final int since) {
        String url = URL_USERS;
        if (since > 0) {
            url += "?since=" + since;
        }

        if (Helper.isOnline(context)) {
            Response.Listener<User[]> resultListener = new Response.Listener<User[]>() {
                @Override
                public void onResponse(User[] response) {
                    if (since == 0) {
                        SQLiteDatabase db = Helper.db(context);
                        db.delete("users", null, null);
                        db.close();
                    }
                    for (User u : response) {
                        u.store(context);
                    }
                    listener.onResponse(response);
                }
            };

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
            Cursor c = db.query("users", null, "id > ?", new String[]{ Integer.toString(since) }, null, null, "id");
            ArrayList<User> users = new ArrayList<>();
            if (c.moveToFirst()) {
                do {
                    byte[] data = c.getBlob(c.getColumnIndex("data"));
                    final Parcel parcel = Parcel.obtain();
                    parcel.unmarshall(data, 0, data.length);
                    parcel.setDataPosition(0);
                    User user = new User(parcel);
                    parcel.recycle();
                    users.add(user);
                } while (c.moveToNext());
            }
            db.close();

            User[] response = new User[users.size()];
            response = users.toArray(response);
            listener.onResponse(response);
        }
    }

    public void store(Context context) {
        final Parcel parcel = Parcel.obtain();
        writeToParcel(parcel, 0);
        byte[] data = parcel.marshall();
        parcel.recycle();

        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("data", data);
        SQLiteDatabase db = Helper.db(context);
        db.insert("users", null, values);
    }

    public void storeAsContrib(Context context, int sort) {
        final Parcel parcel = Parcel.obtain();
        writeToParcel(parcel, 0);
        byte[] data = parcel.marshall();
        parcel.recycle();

        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("repo_id", contrib_id);
        values.put("sort", sort);
        values.put("data", data);
        SQLiteDatabase db = Helper.db(context);
        db.insert("repos_contribs", null, values);
    }

    public void getRepos(final Context context, final Response.Listener<Repo[]> listener) {
        String url = URL_USERS;
        url += "/" + login + "/repos";

        if (Helper.isOnline(context)) {
            Response.Listener<Repo[]> resultListener = new Response.Listener<Repo[]>() {
                @Override
                public void onResponse(Repo[] response) {
                    SQLiteDatabase db = Helper.db(context);
                    db.delete("repos", "user_id = ?", new String[]{ Integer.toString(id) });
                    db.close();
                    for (Repo r : response) {
                        r.user_id = id;
                        r.store(context);
                    }
                    listener.onResponse(response);
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(context);
            GsonRequest<Repo[]> jsObjRequest = new GsonRequest<>(
                url,
                Repo[].class,
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
            Cursor c = db.query("repos", null, "user_id = ?", new String[]{ Integer.toString(id) }, null, null, "full_name");
            ArrayList<Repo> repos = new ArrayList<>();
            if (c.moveToFirst()) {
                do {
                    byte[] data = c.getBlob(c.getColumnIndex("data"));
                    final Parcel parcel = Parcel.obtain();
                    parcel.unmarshall(data, 0, data.length);
                    parcel.setDataPosition(0);
                    Repo repo = new Repo(parcel);
                    parcel.recycle();
                    repos.add(repo);
                } while (c.moveToNext());
            }
            db.close();

            Repo[] response = new Repo[repos.size()];
            response = repos.toArray(response);
            listener.onResponse(response);
        }
    }

    public void setAvatarToView(Context context, ImageView v) {
        if (login.contains("h")) {
            Bitmap bitmap = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);
            bitmap.eraseColor(Color.rgb(194, 167, 124));
            Canvas canvas = new Canvas(bitmap);
            Paint p = new Paint();
            p.setTextAlign(Paint.Align.CENTER);
            p.setColor(Color.BLACK);
            p.setTextSize(40);
            canvas.drawText(login.substring(0, 2), 25, 37, p);
            canvas.setBitmap(bitmap);
            v.setImageBitmap(getCroppedBitmap(bitmap));
        }
        else {
            Picasso.with(context).load(avatar_url)
                .fit()
                .transform(new CircleTransform())
                .into(v);
        }
    }

    public static Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        bitmap.recycle();
        return output;
    }

    public class CircleTransform implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            return User.getCroppedBitmap(source);
        }

        @Override
        public String key() {
            return "circle";
        }
    }
}