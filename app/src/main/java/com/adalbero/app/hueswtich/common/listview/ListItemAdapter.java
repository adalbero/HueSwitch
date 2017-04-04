package com.adalbero.app.hueswtich.common.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by Adalbero on 04/04/2017.
 */

public class ListItemAdapter extends ArrayAdapter<ListItem> {
    private final Context mContext;
    private final LayoutInflater mInflater;
    private final int mResource;

    public ListItemAdapter(Context context, int resource, List<ListItem> data) {
        super(context, resource, data);

        mContext = context;
        mInflater = LayoutInflater.from(context);
        mResource = resource;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        return createViewFromResource(position, v, parent);
    }

    private View createViewFromResource(int position, View v, ViewGroup parent) {
        ListItem item = getItem(position);

        if (v == null) {
            v = mInflater.inflate(mResource, parent, false);
            item.initView(v);
        }

        item.updateView(v);

        return v;
    }

}
