package biz.mesto.anaken.githubclient;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class UsersListAdapter<E> extends ArrayListAdapter<E> {

    OnUserSelectedListener onUserSelectedListener;

    UsersListAdapter(Context context, int resource, ArrayList<E> objects) {
        super(context, resource, objects);
    }

    @Override
    protected String getItemSearchText(int position) {
        return ((User)objects.get(position)).login;
    }

    @Override
    public void buildView(View view, int position) {
        User u = (User)getItem(position);

        View.OnClickListener nameOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onUserSelectedListener != null) {
                    User u = (User)getItem((Integer) ((View)v.getParent()).getTag());
                    onUserSelectedListener.onUserLoginSelected(u);
                }
            }
        };

        ImageView avatarView = (ImageView)view.findViewById(R.id.ivUserAvatar);
        avatarView.setOnClickListener(nameOnClickListener);

        u.setAvatarToView(context, avatarView);

        TextView loginView = (TextView)view.findViewById(R.id.tvUser);
        loginView.setText(u.login);
        loginView.setOnClickListener(nameOnClickListener);

        Button btn = (Button) view.findViewById(R.id.btnUserGo);
        btn.setTag(position);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User u = (User)getItem((Integer) ((View)v.getParent()).getTag());
                Toast.makeText(context, "Переходим к профилю", Toast.LENGTH_SHORT).show();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(u.html_url));
                context.startActivity(browserIntent);
            }
        });
    }

    public interface OnUserSelectedListener {
        public void onUserLoginSelected(User user);
    }

    public void setOnUserSelectedListener(OnUserSelectedListener listener) {
        onUserSelectedListener = listener;
    }
}