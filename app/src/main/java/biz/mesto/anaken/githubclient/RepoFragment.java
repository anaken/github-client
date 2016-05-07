package biz.mesto.anaken.githubclient;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

import java.util.ArrayList;
import java.util.Arrays;

public class RepoFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    View view;
    Repo repo;
    TextView repoName;
    TextView repoDesc;
    ProgressBar progressBar;
    Boolean loadingContribs;
    Boolean loadingCommits;
    ArrayList<User> contributors;
    ArrayList<RepoCommit> commits;
    LayoutInflater inflater;
    SwipeRefreshLayout refreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        view = inflater.inflate(R.layout.repo_fragment, container, false);

        contributors = new ArrayList<>();
        commits = new ArrayList<>();

        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.repo_swipe_container);
        if (refreshLayout != null) {
            refreshLayout.setOnRefreshListener(this);
        }

        repoName = (TextView)view.findViewById(R.id.tvRepoMainName);
        repoDesc = (TextView)view.findViewById(R.id.tvRepoMainDesc);
        progressBar = (ProgressBar)view.findViewById(R.id.repoProgressBar);

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
                buildContribsList(response);
                stopLoading();
            }
        });
        repo.getCommits(getActivity(), new Response.Listener<RepoCommit[]>() {
            @Override
            public void onResponse(RepoCommit[] response) {
                buildCommitsList(response);
                stopLoading();
            }
        });
    }

    private void buildContribsList(User[] response) {
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
        LinearLayout linearLayout = (LinearLayout)view.findViewById(R.id.lvRepoUsersTop);
        LinearLayout.LayoutParams rowContainerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int cols = 1;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            cols = 2;
        }
        View v;
        for (int i = 0; i < contributors.size(); i += cols) {
            LinearLayout rowContainer = new LinearLayout(getActivity());
            rowContainer.setOrientation(LinearLayout.HORIZONTAL);
            rowContainer.setWeightSum((float)cols);

            v = buildContribView(i);
            v.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            rowContainer.addView(v);

            if (cols > 1) {
                if (i + 1 < contributors.size()) {
                    v = buildContribView(i + 1);
                }
                else {
                    v = new View(getActivity());
                }
                v.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                rowContainer.addView(v);
            }

            linearLayout.addView(rowContainer, rowContainerParams);
        }
        loadingContribs = false;
    }

    private void buildCommitsList(RepoCommit[] response) {
        commits.addAll(Arrays.asList(response));
        LinearLayout linearLayout = (LinearLayout)view.findViewById(R.id.llRepoCommits);
        LinearLayout.LayoutParams rowContainerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int cols = 1;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            cols = 2;
        }
        View v;
        for (int i = 0; i < commits.size(); i += 2) {
            LinearLayout rowContainer = new LinearLayout(getActivity());
            rowContainer.setOrientation(LinearLayout.HORIZONTAL);
            rowContainer.setWeightSum((float)cols);

            v = buildCommitView(i);
            v.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            rowContainer.addView(v);

            if (cols > 1) {
                if (i + 1 < commits.size()) {
                    v = buildCommitView(i + 1);
                }
                else {
                    v = new View(getActivity());
                }
                v.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                rowContainer.addView(v);
            }

            linearLayout.addView(rowContainer, rowContainerParams);
        }
        loadingCommits = false;
    }

    private View buildContribView(int position) {
        View view = inflater.inflate(R.layout.repo_contribs_item, null);
        view.setTag(position);

        User user = contributors.get(position);
        TextView textView = (TextView)view.findViewById(R.id.tvRepoContribName);
        textView.setText(user.login);

        return view;
    }

    private View buildCommitView(int position) {
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
            repo = savedInstanceState.getParcelable("repo");
            ArrayList<User> contributors = savedInstanceState.getParcelableArrayList("contributors");
            ArrayList<RepoCommit> commits = savedInstanceState.getParcelableArrayList("commits");
            if (repo != null) {
                repoName.setText(repo.name);
                repoDesc.setText(repo.description);
            }
            if (contributors != null) {
                buildContribsList(contributors.toArray(new User[contributors.size()]));
            }
            if (commits != null) {
                buildCommitsList(commits.toArray(new RepoCommit[commits.size()]));
            }
            stopLoading();
        }
    }

    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
        savedState.putParcelable("repo", repo);
        savedState.putParcelableArrayList("contributors", contributors);
        savedState.putParcelableArrayList("commits", commits);
    }

    @Override
    public void onRefresh() {
        if (Helper.isOnline(getContext())) {
            if (repo != null) {
                contributors.clear();
                commits.clear();
                drawLists();
            }
        }
        else {
            Toast.makeText(getContext(), "Ошибка подключения к серверу", Toast.LENGTH_SHORT).show();
        }
        refreshLayout.setRefreshing(false);
    }
}
