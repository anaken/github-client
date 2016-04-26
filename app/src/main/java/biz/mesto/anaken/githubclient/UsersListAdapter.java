package biz.mesto.anaken.githubclient;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class UsersListAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    User[] objects;

    UsersListAdapter(Context context, User[] products) {
        ctx = context;
        objects = products;
        lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return objects.length;
    }

    @Override
    public Object getItem(int position) {
        return objects[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.users_list_item, parent, false);
        }

        User u = objects[position];

        ImageView avatarView = (ImageView)view.findViewById(R.id.imageView);
        Picasso.with(ctx).load(u.avatar_url)
            .resize(50, 50)
            .transform(new CircleTransform())
            .into(avatarView);
        ((TextView) view.findViewById(R.id.textView)).setText(u.login);
        Button btn = (Button) view.findViewById(R.id.button);
        btn.setTag(position);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User u = objects[(Integer) v.getTag()];
                Toast.makeText(ctx, "Переходим к профилю", Toast.LENGTH_SHORT).show();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(u.html_url));
                ctx.startActivity(browserIntent);
            }
        });

        return view;
    }
}