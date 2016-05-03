package biz.mesto.anaken.githubclient;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;

import java.util.ArrayList;
import java.util.Arrays;

public class RepoFragment extends Fragment {

    View view;
    Repo repo;
    TextView repoName;
    TextView repoDesc;
    ProgressBar progressBar;
    Boolean loadingContribs;
    Boolean loadingCommits;
    ListView lvRepoUsersTop;
    RepoContribsAdapter lvRepoUsersTopAdapter;
    ListView lvRepoCommits;
    RepoCommitsAdapter lvRepoCommitsAdapter;
    ArrayList<User> contributors;
    ArrayList<RepoCommit> commits;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.repo_fragment, container, false);

        contributors = new ArrayList<>();
        commits = new ArrayList<>();

        repoName = (TextView)view.findViewById(R.id.tvRepoMainName);
        repoDesc = (TextView)view.findViewById(R.id.tvRepoMainDesc);
        progressBar = (ProgressBar)view.findViewById(R.id.repoProgressBar);

        lvRepoUsersTop = (ListView)view.findViewById(R.id.lvRepoUsersTop);
        lvRepoUsersTopAdapter = new RepoContribsAdapter<>(getActivity(), R.layout.repo_contribs_item);
        lvRepoUsersTop.setAdapter(lvRepoUsersTopAdapter);

        lvRepoCommits = (ListView)view.findViewById(R.id.lvRepoCommits);
        lvRepoCommitsAdapter = new RepoCommitsAdapter<>(getActivity(), R.layout.repo_commits_item, commits);

        if (savedInstanceState == null) {

        }

        return view;
    }

    public void setRepo(Repo setRepo) {
        repo = setRepo;

        repoName.setText(repo.name);
        repoDesc.setText(repo.description);

        drawLists();
    }

    private void drawLists() {
        startLoading();
        loadingContribs = true;
        loadingCommits = true;
        repo.getContributors(getActivity(), new Response.Listener<User[]>() {
            @Override
            public void onResponse(User[] response) {
                contributors.addAll(Arrays.asList(response));
                lvRepoUsersTopAdapter.addAll(contributors);
                loadingContribs = false;
                stopLoading();
            }
        });
        repo.getCommits(getActivity(), new Response.Listener<RepoCommit[]>() {
            @Override
            public void onResponse(RepoCommit[] response) {
                loadingCommits = false;
                stopLoading();
            }
        });
    }

    private void startLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void stopLoading() {
        if ( ! loadingCommits && ! loadingContribs) {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            Repo r = savedInstanceState.getParcelable("repo");
            ArrayList<User> contributors = savedInstanceState.getParcelableArrayList("contributors");
            setRepo(r);
        }
    }

    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
        savedState.putParcelable("repo", repo);
        savedState.putParcelableArrayList("contributors", contributors);
    }
}
