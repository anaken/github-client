package biz.mesto.anaken.githubclient;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

public class UsersListFragment extends Fragment {

    View view;
    UsersListAdapter usersListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.users_list_fragment, container, false);

        String url = "https://api.github.com/users";

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        GsonRequest<User[]> jsObjRequest = new GsonRequest<>(
            url,
            User[].class,
            null,
            new Response.Listener<User[]>() {
                @Override
                public void onResponse(User[] response) {
                    usersListAdapter = new UsersListAdapter(getActivity(), response);

                    ListView lvMain = (ListView) view.findViewById(R.id.listView);
                    lvMain.setAdapter(usersListAdapter);

//                    String text = "";
//                    for (User user : response) {
//                        text += user.login + "\n";
//                    }
                    Toast.makeText(getActivity(), "Юзверов: " + response.length, Toast.LENGTH_SHORT).show();
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getActivity(), "Ошибка запроса к серверу", Toast.LENGTH_SHORT).show();
                }
            }
        );

        requestQueue.add(jsObjRequest);

        return view;
    }
}
