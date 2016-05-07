package biz.mesto.anaken.githubclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class RepoActivity extends FragmentActivity {

    public static final String EXTRA_REPO    = "repo";
    public static final String EXTRA_NOTICED = "notice";

    Repo repo;
    Boolean noticed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repo);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            Bundle args = intent.getExtras();
            repo = args.getParcelable(EXTRA_REPO);
            noticed = args.getBoolean(EXTRA_NOTICED);

            if (repo != null) {
                RepoFragment repoFragment = (RepoFragment) getSupportFragmentManager().findFragmentById(R.id.repo_fragment);
                repoFragment.setRepo(repo);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (noticed != null) {
            System.exit(0);
        }
        super.onBackPressed();
    }
}
