package biz.mesto.anaken.githubclient;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ReposListAdapter<E> extends ArrayListAdapter<E> {

    OnRepoClickedListener onRepoClickedListener;

    ReposListAdapter(Context context, int resource, ArrayList<E> objects) {
        super(context, resource, objects);
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
        int rate = repo.getRate(context);
        ratingBar.setRating(rate >= 0 ? rate : 0);
        ratingBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                RatingBar ratingBar = (RatingBar)v;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int rate = ratingBar.getRating() == 1 ? 0 : 1;
                    Repo repo = (Repo)getItem((Integer) ((View)v.getParent()).getTag());
                    repo.setRate(context, rate);
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
}
