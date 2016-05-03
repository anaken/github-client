package biz.mesto.anaken.githubclient;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

public class ReposProvider {

    final private static String URL = "https://api.github.com/users";

    public static void getRepos(final Context context, Response.Listener<Repo[]> listener, User user) {
        String url = URL;
        url += "/" + user.login + "/repos";

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        GsonRequest<Repo[]> jsObjRequest = new GsonRequest<>(
                url,
                Repo[].class,
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

}
