package biz.mesto.anaken.githubclient;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

import java.util.ArrayList;
import java.util.Arrays;

public class ReposListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    View view;
    User user;
    TextView userName;
    ImageView avatarView;
    HeaderGridView lvRepoList;
    ProgressBar progressBar;
    ArrayList<Repo> repos;
    ReposListAdapter reposListAdapter;
    OnRepoSelectedListener onRepoSelectedListener;
    String searchQuery;
    SwipeRefreshLayout refreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.repos_list_fragment, container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.reposProgressBar);

        repos = new ArrayList<>();

        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.repos_swipe_container);
        if (refreshLayout != null) {
            refreshLayout.setOnRefreshListener(this);
        }

        View vRepoListHeader = inflater.inflate(R.layout.repos_list_header, null);
        userName = (TextView) vRepoListHeader.findViewById(R.id.tvUserName);
        avatarView = (ImageView) vRepoListHeader.findViewById(R.id.avatarImageView);

        lvRepoList = (HeaderGridView) view.findViewById(R.id.lvRepoList);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            lvRepoList.setNumColumns(2);
        }
        lvRepoList.addHeaderView(vRepoListHeader);
        reposListAdapter = new ReposListAdapter<>(getActivity(), R.layout.repos_list_item, repos);
        reposListAdapter.setOnRepoClickedListener(new ReposListAdapter.OnRepoClickedListener() {
            @Override
            public void onRepoClicked(Repo repo) {
                onRepoSelected(repo);
            }
        });
        lvRepoList.setAdapter(reposListAdapter);

        if (savedInstanceState == null) {

        }

        return view;
    }

    public void setUser(User u) {
        user = u;
        userName.setText(u.login);
        user.setAvatarToView(getActivity(), avatarView);
        drawReposList(u);
    }

    public void setUser(User u, ArrayList<Repo> setRepos) {
        user = u;
        userName.setText(u.login);
        user.setAvatarToView(getActivity(), avatarView);
        setRepos(setRepos);
        setLoading(false);
    }

    private void drawReposList(User u) {
        setLoading(true);
        u.getRepos(getActivity(), new Response.Listener<Repo[]>() {
            @Override
            public void onResponse(Repo[] responseRepos) {
                setRepos(responseRepos);
                setLoading(false);
            }
        });
    }

    private void setLoading(Boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.INVISIBLE);
    }

    private void setRepos(ArrayList<Repo> setRepos) {
        if (setRepos != null) {
            repos.addAll(setRepos);
        }
        reposListAdapter.notifyDataSetChanged();
    }

    private void setRepos(Repo[] setRepos) {
        repos.addAll(Arrays.asList(setRepos));
        reposListAdapter.notifyDataSetChanged();
    }

    public void search(String query) {
        searchQuery = query;
        reposListAdapter.search(query);
    }

    public interface OnRepoSelectedListener {
        public void onRepoSelected(Repo repo);
    }

    public void onRepoSelected(Repo repo) {
        if (onRepoSelectedListener != null) {
            onRepoSelectedListener.onRepoSelected(repo);
        }
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            onRepoSelectedListener = (OnRepoSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement UsersListFragment.OnUserSelectedListener.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onRepoSelectedListener = null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            User u = savedInstanceState.getParcelable("user");
            ArrayList<Repo> setRepos = savedInstanceState.getParcelableArrayList("repos");
            setUser(u, setRepos);
        }
    }

    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
        savedState.putParcelable("user", user);
        savedState.putParcelableArrayList("repos", repos);
    }

    @Override
    public void onRefresh() {
        if (Helper.isOnline(getContext())) {
            if (user != null) {
                repos.clear();
                drawReposList(user);
            }
        }
        else {
            Toast.makeText(getContext(), "Ошибка подключения к серверу", Toast.LENGTH_SHORT).show();
        }
        refreshLayout.setRefreshing(false);
    }
}
