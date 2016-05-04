package biz.mesto.anaken.githubclient;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class RepoCommitsAdapter<E> extends ArrayListAdapter<E> {

    RepoCommitsAdapter(Context context, int resource, ArrayList<E> objects) {
        super(context, resource, objects);
    }

    @Override
    public void buildView(View view, int position) {
        RepoCommit commit = (RepoCommit) getItem(position);

        TextView tvCommitText = (TextView) view.findViewById(R.id.tvCommitText);
        tvCommitText.setText(commit.commit.message);

        TextView tvCommitAuthor = (TextView) view.findViewById(R.id.tvCommitAuthor);
        tvCommitAuthor.setText(commit.getAuthorName());

        TextView tvCommitDate = (TextView) view.findViewById(R.id.tvCommitDate);
        tvCommitDate.setText(commit.commit.author.date);
    }
}
