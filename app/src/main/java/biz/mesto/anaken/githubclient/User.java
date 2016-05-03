package biz.mesto.anaken.githubclient;

import android.content.Context;
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

import com.google.gson.annotations.SerializedName;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class User implements Parcelable {
    @SerializedName("login") public String login;
    @SerializedName("id") public int id;
    @SerializedName("url") public String url;
    @SerializedName("html_url") public String html_url;
    @SerializedName("repos_url") public String repos_url;
    @SerializedName("avatar_url") public String avatar_url;

    public User(Parcel parcel) {
        login = parcel.readString();
        id = parcel.readInt();
        url = parcel.readString();
        html_url = parcel.readString();
        repos_url = parcel.readString();
        avatar_url = parcel.readString();
    }

    public User(HashMap params) {
        id = Integer.parseInt(params.get("id").toString());
        login = params.get("login").toString();
        avatar_url = params.get("avatar_url").toString();
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

    public Bitmap getCroppedBitmap(Bitmap bitmap) {
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
        return output;
    }
}