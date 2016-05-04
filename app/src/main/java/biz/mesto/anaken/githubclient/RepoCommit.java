package biz.mesto.anaken.githubclient;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class RepoCommit implements Parcelable {

    @SerializedName("sha") public String sha;
    @SerializedName("url") public String url;
    @SerializedName("commit_text") public String commit_text;
    @SerializedName("commit_author") public String commit_author;
    @SerializedName("author_login") public String author_login;
    @SerializedName("commit_date") public String commit_date;
    public RepoCommitAuthor author;
    public RepoCommitCommit commit;

    protected RepoCommit(Parcel parcel) {
        sha = parcel.readString();
        commit_text = parcel.readString();
        commit_author = parcel.readString();
        author_login = parcel.readString();
        commit_date = parcel.readString();
        url = parcel.readString();
        commit.message = commit_text;
        commit.author.name = commit_author;
        author.login = author_login;
        commit.author.date = commit_date;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sha);
        dest.writeString(commit.message);
        dest.writeString(commit.author.name);
        dest.writeString(author != null ? author.login : commit.author.name);
        dest.writeString(commit.author.date);
        dest.writeString(url);
    }

    public String getAuthorName() {
        return author != null ? author.login : commit.author.name;
    }

    public void download(Context context) {
        String downloadUrl = url.replace("commits", "archive") + ".zip";
        String localName = sha + ".zip";
        DownloadService.startActionDownload(context, downloadUrl, localName);
    }

    @Override
    public int describeContents() {
        return 0;
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

class RepoCommitAuthor {
    public String login;
}