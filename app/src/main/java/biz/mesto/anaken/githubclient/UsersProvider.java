package biz.mesto.anaken.githubclient;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

public class UsersProvider {

    final private static String URL = "https://api.github.com/users";

    public static void getUsers(final Context context, Response.Listener<User[]> listener, int since) {
        String url = URL;
        if (since > 0) {
            url += "?since=" + since;
        }

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

    public static void getUsers(Context context, Response.Listener<User[]> listener) {
        UsersProvider.getUsers(context, listener, 0);
    }

}
