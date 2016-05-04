package biz.mesto.anaken.githubclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Arrays;

abstract public class ArrayListAdapter<E> extends BaseAdapter {

    Context context;
    LayoutInflater lInflater;
    ArrayList<E> objects;
    int resource;

    ArrayListAdapter(Context context, int resource, ArrayList<E> objects) {
        this.context = context;
        this.resource = resource;
        this.objects = objects;
        lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    ArrayListAdapter(Context context, int resource) {
        this(context, resource, new ArrayList<E>());
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public E getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(resource, parent, false);
        }
        view.setTag(position);

        buildView(view, position);

        return view;
    }

    abstract public void buildView(View view, int position);

    public void addAll(E[] objects) {
        this.objects.addAll(Arrays.asList(objects));
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<? extends E> objects) {
        this.objects.addAll(objects);
        notifyDataSetChanged();
    }
}
