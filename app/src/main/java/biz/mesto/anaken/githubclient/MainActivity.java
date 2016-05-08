package biz.mesto.anaken.githubclient;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
        implements UsersListFragment.OnUserSelectedListener {

    final static int DB_VERSION = 5;

    MenuItem searchItem;
    Bundle savedState;
    String searchQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        savedState = savedInstanceState;

        Toolbar mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mActionBarToolbar);

        registerReceiver(receiver, new IntentFilter(DownloadService.CHANNEL));

        startService(new Intent(this, BackgroundService.class));

        startService(new Intent(this, RegistrationIntentService.class));
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

    private void searchText(String text) {
        searchQuery = text;
        FragmentManager fragmentManager = getSupportFragmentManager();
        UsersListFragment fragment = (UsersListFragment) fragmentManager.findFragmentById(R.id.users_list);
        fragment.search(searchQuery);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(receiver);
    }

    private void showUser(User user) {
        Intent intent = new Intent(this, ReposActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    @Override
    public void onUserSelected(User user) {
        showUser(user);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };

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
