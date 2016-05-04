package biz.mesto.anaken.githubclient;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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
    ArrayList<User> contributors;
    ArrayList<RepoCommit> commits;
    LayoutInflater inflater;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        view = inflater.inflate(R.layout.repo_fragment, container, false);

        contributors = new ArrayList<>();
        commits = new ArrayList<>();

        repoName = (TextView)view.findViewById(R.id.tvRepoMainName);
        repoDesc = (TextView)view.findViewById(R.id.tvRepoMainDesc);
        progressBar = (ProgressBar)view.findViewById(R.id.repoProgressBar);

        lvRepoUsersTop = (ListView)view.findViewById(R.id.lvRepoUsersTop);
        lvRepoUsersTopAdapter = new RepoContribsAdapter<>(getActivity(), R.layout.repo_contribs_item);
        lvRepoUsersTop.setAdapter(lvRepoUsersTopAdapter);

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
                ArrayList<User> sublist = new ArrayList<>();
                int max = 5;
                int n = 1;
                for (User u : response) {
                    if (n > max) {
                        break;
                    }
                    sublist.add(u);
                    n++;
                }
                contributors.addAll(sublist);
                lvRepoUsersTopAdapter.addAll(sublist);
                loadingContribs = false;
                stopLoading();
            }
        });
        repo.getCommits(getActivity(), new Response.Listener<RepoCommit[]>() {
            @Override
            public void onResponse(RepoCommit[] response) {
                commits.addAll(Arrays.asList(response));
                for (int i = 0; i < commits.size(); i++) {
                    buildCommitView(i);
                }
                loadingCommits = false;
                stopLoading();
            }
        });
    }

    private View buildCommitView(int position) {
        LinearLayout linearLayout = (LinearLayout)view.findViewById(R.id.llRepoCommits);
        View view = inflater.inflate(R.layout.repo_commits_item, null);
        view.setTag(position);

        RepoCommit commit = commits.get(position);

        TextView tvCommitText = (TextView) view.findViewById(R.id.tvCommitText);
        tvCommitText.setText(commit.commit.message);

        TextView tvCommitAuthor = (TextView) view.findViewById(R.id.tvCommitAuthor);
        tvCommitAuthor.setText(commit.getAuthorName());

        TextView tvCommitDate = (TextView) view.findViewById(R.id.tvCommitDate);
        tvCommitDate.setText(Helper.dateFormat(commit.commit.author.date));

        Button btnCommitDownload = (Button) view.findViewById(R.id.btnCommitDownload);
        btnCommitDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (Integer) ((View)v.getParent()).getTag();
                RepoCommit commit = commits.get(position);
                commit.download(getActivity());
            }
        });

        linearLayout.addView(view);

        return view;
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
