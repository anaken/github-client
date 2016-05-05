package biz.mesto.anaken.githubclient;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Helper {
    public static String dateFormat(String date) {
        Locale local = new Locale("ru","RU");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat dstFormat = new SimpleDateFormat("hh:mm dd MMM yyyy", local);
        String result = "";
        try {
            Date convertedDate = dateFormat.parse(date.replace("T", " ").substring(0, 19));
            result = dstFormat.format(convertedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static SQLiteDatabase db(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        return dbHelper.getWritableDatabase();
    }
}
