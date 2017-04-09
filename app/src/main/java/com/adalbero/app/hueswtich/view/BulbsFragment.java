package com.adalbero.app.hueswtich.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.adalbero.app.hueswtich.R;
import com.adalbero.app.hueswtich.common.listview.ListItem;
import com.adalbero.app.hueswtich.common.listview.ListItemAdapter;
import com.adalbero.app.hueswtich.controller.AppController;
import com.adalbero.app.hueswtich.controller.AppListener;
import com.adalbero.app.hueswtich.data.BulbItem;
import com.adalbero.app.hueswtich.data.ResourceItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adalbero on 04/04/2017.
 */

public class BulbsFragment extends Fragment implements AppListener {
    private ListView mListView;
    private ListItemAdapter mAdapter;
    private AppController mAppController;

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

        mAppController = AppController.getInstance();
        mAppController.registerAppListener(this);

        onDataChanged(true);

        return v;
    }

    @Override
    public void onDataChanged(boolean bInit) {
        if (mAdapter != null) {
            if (bInit || mAdapter.getCount() == 0) {
                List<ResourceItem> data = mAppController.getData();

                mAdapter.clear();
                for (ListItem item : data) {
                    if (item instanceof BulbItem) {
                        mAdapter.add(item);
                    }
                }
            } else {
                mAdapter.notifyDataSetChanged();
            }
        }
    }
}
