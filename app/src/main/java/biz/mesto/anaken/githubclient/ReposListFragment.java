package biz.mesto.anaken.githubclient;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.android.volley.Response;

import java.util.ArrayList;
import java.util.Arrays;

public class ReposListFragment extends Fragment {

    View view;
    User user;
    ImageView avatarView;
    ListView lvRepoList;
    ProgressBar progressBar;
    ArrayList<Repo> repos;
    ReposListAdapter reposListAdapter;
    OnRepoSelectedListener onRepoSelectedListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.repos_list_fragment, container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.reposProgressBar);
        avatarView = (ImageView) view.findViewById(R.id.avatarImageView);

        repos = new ArrayList<>();

        lvRepoList = (ListView) view.findViewById(R.id.lvRepoList);
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
        user.setAvatarToView(getActivity(), avatarView);
        drawReposList(u);
    }

    public void setUser(User u, ArrayList<Repo> setRepos) {
        user = u;
        user.setAvatarToView(getActivity(), avatarView);
        setRepos(setRepos);
        setLoading(false);
    }

    private void drawReposList(User u) {
        setLoading(true);
        ReposProvider.getRepos(getActivity(), new Response.Listener<Repo[]>() {
            @Override
            public void onResponse(Repo[] responseRepos) {
                setRepos(responseRepos);
                setLoading(false);
            }
        }, u);
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
}
