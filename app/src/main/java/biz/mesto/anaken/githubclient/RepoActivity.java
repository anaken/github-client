package biz.mesto.anaken.githubclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class RepoActivity extends FragmentActivity {

    Repo repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repo);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            Bundle args = intent.getExtras();
            repo = args.getParcelable("repo");

            if (repo != null) {
                RepoFragment repoFragment = (RepoFragment) getSupportFragmentManager().findFragmentById(R.id.repo_fragment);
                repoFragment.setRepo(repo);
            }
        }
    }
}
