package com.adalbero.app.hueswtich;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.adalbero.app.hueswtich.common.listview.ListItem;
import com.adalbero.app.hueswtich.common.listview.ListItemAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adalbero on 04/04/2017.
 */

public class GroupsFragment extends Fragment {
    private ListView mListView;
    private ListItemAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_groups, container, false);

        List<ListItem> data = new ArrayList<>();
        mAdapter = new ListItemAdapter(getActivity(), R.layout.item_group, data);

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
            mAdapter.notifyDataSetChanged();
        }
    }

    public void updateData() {
        if (mAdapter != null) {
            List<ResourceItem> data = ((MainActivity)getActivity()).getData();

            mAdapter.clear();
            for (ListItem item : data) {
                if (item instanceof GroupItem) {
                    mAdapter.add(item);
                }
            }
        }
    }


}
