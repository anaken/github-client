package biz.mesto.anaken.githubclient;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

public class ReposActivity extends FragmentActivity implements ReposListFragment.OnRepoSelectedListener {

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repos);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            Bundle args = intent.getExtras();
            user = args.getParcelable("user");

            if (user != null) {
                ReposListFragment reposListFragment = (ReposListFragment) getSupportFragmentManager().findFragmentById(R.id.repos_fragment);
                reposListFragment.setUser(user);
            }
        }
    }

    private void showRepo(Repo repo) {
        Intent intent = new Intent(this, RepoActivity.class);
        intent.putExtra("repo", repo);
        startActivity(intent);
    }

    @Override
    public void onRepoSelected(Repo repo) {
        showRepo(repo);
    }
}
