package biz.mesto.anaken.githubclient;


import android.content.Context;
import android.view.View;
import android.widget.TextView;

public class RepoContribsAdapter<E> extends ArrayListAdapter<E> {

    RepoContribsAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public void buildView(View view, int position) {
        User user = (User)getItem(position);
        TextView textView = (TextView)view.findViewById(R.id.tvRepoContribName);
        textView.setText(user.login);
    }
}
