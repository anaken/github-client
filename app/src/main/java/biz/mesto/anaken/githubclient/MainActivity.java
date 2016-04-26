package biz.mesto.anaken.githubclient;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class MainActivity extends FragmentActivity {

    private UsersListFragment usersListFragment;

    private FragmentManager manager;
    private FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        usersListFragment = new UsersListFragment();
        transaction.add(R.id.container, usersListFragment);
        transaction.commit();

//        ImageView imgView = (ImageView) findViewById(R.id.imageView);
        //Picasso.with(this).load("http://i.imgur.com/DvpvklR.png").transform(new CircleTransform()).into(imgView);
    }
}
