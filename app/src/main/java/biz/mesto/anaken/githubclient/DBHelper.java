package biz.mesto.anaken.githubclient;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, "githubDB", null, 1);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table repos_rates (name text, rate integer);");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}