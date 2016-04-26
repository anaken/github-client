package biz.mesto.anaken.githubclient;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("login") public String login;
    @SerializedName("id") public int id;
    @SerializedName("url") public String url;
    @SerializedName("html_url") public String html_url;
    @SerializedName("repos_url") public String repos_url;
    @SerializedName("avatar_url") public String avatar_url;
}