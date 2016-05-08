package biz.mesto.anaken.githubclient;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ReposListAdapter<E> extends ArrayListAdapter<E> {

    private ArrayList<String> subsRepos;

    OnRepoClickedListener onRepoClickedListener;

    ReposListAdapter(Context context, int resource, ArrayList<E> objects) {
        super(context, resource, objects);
    }

    @Override
    protected String getItemSearchText(int position) {
        return ((Repo) objects.get(position)).name;
    }

    @Override
    public void buildView(View view, int position) {

        final Repo repo = (Repo)getItem(position);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Repo repo = (Repo)getItem((Integer) ((View)v.getParent()).getTag());
                onRepoClicked(repo);
            }
        };
        TextView nameView = (TextView)view.findViewById(R.id.tvRepoName);
        nameView.setText(repo.name);
        nameView.setOnClickListener(onClickListener);

        TextView descView = (TextView)view.findViewById(R.id.tvRepoDesc);
        descView.setText(repo.description);
        descView.setOnClickListener(onClickListener);

        Button btnRepoGo = (Button)view.findViewById(R.id.btnRepoGo);
        btnRepoGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Repo repo = (Repo)getItem((Integer) ((View)v.getParent()).getTag());
                Toast.makeText(context, "Переходим к репозиторию", Toast.LENGTH_SHORT).show();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(repo.html_url));
                context.startActivity(browserIntent);
            }
        });

        TextView dateView = (TextView)view.findViewById(R.id.tvRepoUpdated);
        dateView.setText(Helper.dateFormat(repo.updated_at));

        RatingBar ratingBar = (RatingBar)view.findViewById(R.id.ivRepoStar);
        ratingBar.setRating((float) isRepoSubscribed(repo.full_name));
        ratingBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                RatingBar ratingBar = (RatingBar)v;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int rate = ratingBar.getRating() == 1 ? 0 : 1;
                    Repo repo = (Repo)getItem((Integer) ((View)v.getParent()).getTag());
                    repo.setRate(context, rate, null);
                    ratingBar.setRating(rate);
                    return true;
                }
                return true;
            }
        });
    }

    public interface OnRepoClickedListener {
        public void onRepoClicked(Repo repo);
    }

    public void onRepoClicked(Repo repo) {
        if (onRepoClickedListener != null) {
            onRepoClickedListener.onRepoClicked(repo);
        }
    }

    public void setOnRepoClickedListener(OnRepoClickedListener listener) {
        onRepoClickedListener = listener;
    }

    public void setSubsRepos(ArrayList<String> reposNames) {
        subsRepos = reposNames;
    }

    private int isRepoSubscribed(String repoName) {
        if (subsRepos == null) {
            return 0;
        }
        for (String name : subsRepos) {
            if (name.equals(repoName)) {
                return 1;
            }
        }
        return 0;
    }
}
