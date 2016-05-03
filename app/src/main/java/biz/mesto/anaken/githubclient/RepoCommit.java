package biz.mesto.anaken.githubclient;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class RepoCommit implements Parcelable {

    @SerializedName("sha") public String sha;
    public RepoCommitCommit commit;

    protected RepoCommit(Parcel parcel) {
        sha = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sha);
    }

    public static final Creator<RepoCommit> CREATOR = new Creator<RepoCommit>() {
        @Override
        public RepoCommit createFromParcel(Parcel in) {
            return new RepoCommit(in);
        }

        @Override
        public RepoCommit[] newArray(int size) {
            return new RepoCommit[size];
        }
    };
}

class RepoCommitCommit {
    public RepoCommitCommitAuthor author;
    public String message;
}

class RepoCommitCommitAuthor {
    public String name;
    public String date;
}