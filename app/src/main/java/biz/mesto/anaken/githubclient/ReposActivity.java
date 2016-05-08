package biz.mesto.anaken.githubclient;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

public class ReposActivity extends AppCompatActivity
        implements ReposListFragment.OnRepoSelectedListener {

    User user;

    MenuItem searchItem;
    Bundle savedState;
    String searchQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repos);
        savedState = savedInstanceState;

        Toolbar mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mActionBarToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        if (null != searchView) {
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }

        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String newText) {
                if (newText.length() == 0 && searchQuery != null && searchQuery.length() != 0) {
                    searchText(newText);
                }
                return true;
            }

            public boolean onQueryTextSubmit(String query) {
                if ( ! query.equals(searchQuery)) {
                    searchText(query);
                }
                return true;
            }
        };

        if (searchView != null) {
            searchView.setOnQueryTextListener(queryTextListener);
        }

        return true;
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

    private void searchText(String text) {
        searchQuery = text;
        FragmentManager fragmentManager = getSupportFragmentManager();
        ReposListFragment fragment = (ReposListFragment) fragmentManager.findFragmentById(R.id.repos_fragment);
        fragment.search(searchQuery);
    }

    private void showRepo(Repo repo) {
        Intent intent = new Intent(this, RepoActivity.class);
        intent.putExtra(RepoActivity.EXTRA_REPO, repo);
        startActivity(intent);
    }

    @Override
    public void onRepoSelected(Repo repo) {
        showRepo(repo);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("search_query", searchQuery);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        SearchView searchView = (SearchView) searchItem.getActionView();
        if (savedState != null) {
            String currentQuery = savedState.getString("search_query");
            if (currentQuery != null) {
                if ( ! TextUtils.isEmpty(currentQuery)) {
                    searchItem.expandActionView();
                    searchView.setQuery(currentQuery, true);
                    searchView.clearFocus();
                }
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }
}
