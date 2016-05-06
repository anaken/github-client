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
    ArrayList<Integer> searchObjects;
    int resource;
    String searchQuery;

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
        return getSearchObjects().size();
    }

    @Override
    public E getItem(int position) {
        if (position >= getSearchObjects().size()) {
            return null;
        }
        return objects.get(getSearchObjects().get(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    abstract protected String getItemSearchText(int position);

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

    public void search(String searchQuery) {
        this.searchQuery = searchQuery;
        searchObjects = null;
        notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        searchObjects = null;
        super.notifyDataSetChanged();
    }

    private ArrayList<Integer> getSearchObjects() {
        if (searchObjects == null) {
            searchObjects = new ArrayList<>();
            for (int i = 0; i < objects.size(); i++) {
                if (searchQuery == null || searchQuery.length() == 0 || getItemSearchText(i).contains(searchQuery)) {
                    searchObjects.add(i);
                }
            }
        }
        return searchObjects;
    }
}
