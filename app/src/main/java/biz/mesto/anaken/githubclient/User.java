package biz.mesto.anaken.githubclient;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

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
}