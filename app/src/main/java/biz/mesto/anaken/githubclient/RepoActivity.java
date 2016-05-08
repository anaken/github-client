package biz.mesto.anaken.githubclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class RepoActivity extends AppCompatActivity {

    public static final String EXTRA_REPO    = "repo";
    public static final String EXTRA_NOTICED = "notice";

    Repo repo;
    int noticed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repo);

        Toolbar mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            Bundle args = intent.getExtras();
            repo = args.getParcelable(EXTRA_REPO);
            noticed = args.getInt(EXTRA_NOTICED);

            if (repo != null) {
                RepoFragment repoFragment = (RepoFragment) getSupportFragmentManager().findFragmentById(R.id.repo_fragment);
                repoFragment.setRepo(repo);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (noticed > 0) {
            System.exit(0);
        }
        super.onBackPressed();
    }
}
