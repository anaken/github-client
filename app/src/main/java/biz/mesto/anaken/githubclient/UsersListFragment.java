package biz.mesto.anaken.githubclient;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.android.volley.Response;

import java.util.ArrayList;

public class UsersListFragment extends Fragment {

    View view;
    UsersListAdapter usersListAdapter;
    ListView lvMain;
    ArrayList<User> users;
    Boolean loading = false;
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.users_list_fragment, container, false);
        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);

        users = new ArrayList<>();

        lvMain = (ListView) view.findViewById(R.id.listView);
        lvMain.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (totalItemCount > 0 && (firstVisibleItem + visibleItemCount) >= totalItemCount) {
                    if ( ! loading) {
                        setLoading(true);
                        drawUsersList(users.get(totalItemCount - 1).id);
                    }
                }
            }
        });

        if (savedInstanceState != null) {
            users = savedInstanceState.getParcelableArrayList("users");
            usersListAdapter = new UsersListAdapter(getActivity(), users);
            lvMain.setAdapter(usersListAdapter);
            setLoading(false);
        }
        else {
            drawUsersList(0);
        }

        return view;
    }

    private void drawUsersList(int since) {
        UsersProvider.getUsers(getActivity(), new Response.Listener<User[]>() {
            @Override
            public void onResponse(User[] responseUsers) {
                for (User u : responseUsers) {
                    users.add(u);
                }
                if (usersListAdapter == null) {
                    usersListAdapter = new UsersListAdapter(getActivity(), users);
                    lvMain.setAdapter(usersListAdapter);
                }
                else {
                    usersListAdapter.notifyDataSetChanged();
                }
                setLoading(false);
            }
        }, since);
    }

    private void setLoading(Boolean loads) {
        loading = loads;
        progressBar.setVisibility(loading ? View.VISIBLE : View.INVISIBLE);
    }

    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
        savedState.putParcelableArrayList("users", users);
    }
}
