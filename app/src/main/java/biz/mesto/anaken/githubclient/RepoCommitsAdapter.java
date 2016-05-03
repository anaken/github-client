package biz.mesto.anaken.githubclient;

import android.content.Context;
import android.view.View;

import java.util.ArrayList;

public class RepoCommitsAdapter<E> extends ArrayListAdapter<E> {

    RepoCommitsAdapter(Context context, int resource, ArrayList<E> objects) {
        super(context, resource, objects);
    }

    @Override
    public void buildView(View view, int position) {
        RepoCommit commit = (RepoCommit) getItem(position);
    }
}
