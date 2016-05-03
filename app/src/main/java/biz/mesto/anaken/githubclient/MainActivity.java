package biz.mesto.anaken.githubclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class MainActivity extends FragmentActivity
        implements UsersListFragment.OnUserSelectedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
}
