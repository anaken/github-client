package biz.mesto.anaken.githubclient;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, "githubDB", null, MainActivity.DB_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table repos_subs (name text, ts string);");
        db.execSQL("create table users (id int primary key, data blob);");
        db.execSQL("create table repos (id int, user_id int, full_name string, data blob);");
        db.execSQL("create table repos_contribs (id int, repo_id int, sort int, data blob);");
        db.execSQL("create table repos_commits (sha string, repo_id int, date string, data blob);");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1 && newVersion == 2) {
            db.beginTransaction();
            try {
                db.execSQL("create temporary table tmp_repos_rates (name text);");
                db.execSQL("insert into tmp_repos_rates select name from repos_rates;");
                db.execSQL("create table repos_subs (name text primary key, ts string);");
                db.execSQL("insert into repos_subs (name) select name from tmp_repos_rates;");
                db.execSQL("drop table repos_rates;");
                db.setTransactionSuccessful();
            }
            finally {
                db.endTransaction();
            }
        }

        if (oldVersion == 2 && newVersion == 3) {
            db.beginTransaction();
            try {
                db.execSQL("create table users (id int primary key, data blob);");
                db.execSQL("create table repos (id int, user_id int, full_name string, data blob);");
                db.execSQL("create table repos_contribs (id int, repo_id int, sort int, data blob);");
                db.execSQL("create table repos_commits (sha string, repo_id int, date string, data blob);");
                db.setTransactionSuccessful();
            }
            finally {
                db.endTransaction();
            }
        }
    }
}