package biz.mesto.anaken.githubclient;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Response;

import java.util.ArrayList;
import java.util.Arrays;

public class UsersListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    View view;
    UsersListAdapter usersListAdapter;
    ListView lvMain;
    ArrayList<User> users;
    Boolean loading = false;
    ProgressBar progressBar;
    OnUserSelectedListener onUserSelectedListener;
    String searchQuery;
    SwipeRefreshLayout refreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.users_list_fragment, container, false);
        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);

        users = new ArrayList<>();

        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        if (refreshLayout != null) {
            refreshLayout.setOnRefreshListener(this);
        }

        lvMain = (ListView) view.findViewById(R.id.listView);
        lvMain.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (totalItemCount > 0 && (firstVisibleItem + visibleItemCount) >= totalItemCount) {
                    if ( ! loading && Helper.isOnline(getContext()) && (searchQuery == null || searchQuery.length() == 0)) {
                        setLoading(true);
                        drawUsersList(users.get(totalItemCount - 1).id);
                    }
                }
            }
        });

        usersListAdapter = new UsersListAdapter<>(getActivity(), R.layout.users_list_item, users);
        usersListAdapter.setOnUserSelectedListener(new UsersListAdapter.OnUserSelectedListener() {
            @Override
            public void onUserLoginSelected(User user) {
                onUserSelected(user);
            }
        });
        lvMain.setAdapter(usersListAdapter);

        if (savedInstanceState == null) {
            drawUsersList(0);
        }

        return view;
    }

    private void drawUsersList(int since) {
        User.getUsers(getActivity(), new Response.Listener<User[]>() {
            @Override
            public void onResponse(User[] responseUsers) {
                setUsers(responseUsers);
                setLoading(false);
            }
        }, since);
    }

    private void setLoading(Boolean loads) {
        loading = loads;
        progressBar.setVisibility(loading ? View.VISIBLE : View.INVISIBLE);
    }

    private void setUsers(ArrayList<User> setUsers) {
        if (setUsers != null) {
            users.addAll(setUsers);
        }
        usersListAdapter.notifyDataSetChanged();
    }

    private void setUsers(User[] setUsers) {
        users.addAll(Arrays.asList(setUsers));
        usersListAdapter.notifyDataSetChanged();
    }

    public void search(String query) {
        searchQuery = query;
        usersListAdapter.search(query);
    }

    public interface OnUserSelectedListener {
        public void onUserSelected(User user);
    }

    public void onUserSelected(User u) {
        if (onUserSelectedListener != null) {
            onUserSelectedListener.onUserSelected(u);
        }
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            onUserSelectedListener = (OnUserSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement UsersListFragment.OnUserSelectedListener.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onUserSelectedListener = null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            ArrayList<User> setUsers = savedInstanceState.getParcelableArrayList("users");
            setUsers(setUsers);
            setLoading(false);
        }
    }

    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
        savedState.putParcelableArrayList("users", users);
    }

    @Override
    public void onRefresh() {
        if (Helper.isOnline(getContext())) {
            users.clear();
            drawUsersList(0);
        }
        else {
            Toast.makeText(getContext(), "Ошибка подключения к серверу", Toast.LENGTH_SHORT).show();
        }
        refreshLayout.setRefreshing(false);
    }
}
