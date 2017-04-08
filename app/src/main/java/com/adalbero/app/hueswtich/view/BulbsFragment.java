package com.adalbero.app.hueswtich.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.adalbero.app.hueswtich.MainActivity;
import com.adalbero.app.hueswtich.R;
import com.adalbero.app.hueswtich.common.listview.ListItem;
import com.adalbero.app.hueswtich.common.listview.ListItemAdapter;
import com.adalbero.app.hueswtich.data.BulbItem;
import com.adalbero.app.hueswtich.data.ResourceItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adalbero on 04/04/2017.
 */

public class BulbsFragment extends Fragment {
    private ListView mListView;
    private ListItemAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_bulbs, container, false);

        List<ListItem> data = new ArrayList<>();
        mAdapter = new ListItemAdapter(getActivity(), R.layout.item_bulb, data);

        mListView = (ListView) v.findViewById(R.id.list_view);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                ListItem item = (ListItem) adapter.getItemAtPosition(position);
                item.onClick(v);
            }
        });

        updateData();

        return v;
    }

    public void updateCache() {
        if (mAdapter != null) {
            if (mAdapter.getCount() == 0) {
                updateData();
            } else {
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    public void updateData() {
        if (mAdapter != null) {
            List<ResourceItem> data = ((MainActivity) getActivity()).getData();

            mAdapter.clear();
            for (ListItem item : data) {
                if (item instanceof BulbItem) {
                    mAdapter.add(item);
                }
            }
        }
    }


}
