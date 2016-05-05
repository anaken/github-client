package biz.mesto.anaken.githubclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class MainActivity extends FragmentActivity
        implements UsersListFragment.OnUserSelectedListener {

    final static int DB_VERSION = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registerReceiver(receiver, new IntentFilter(DownloadService.CHANNEL));
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
}
