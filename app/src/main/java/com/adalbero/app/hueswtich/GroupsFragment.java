package com.adalbero.app.hueswtich;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.adalbero.app.hueswtich.common.hue.HueManager;
import com.adalbero.app.hueswtich.common.listview.ListItem;
import com.adalbero.app.hueswtich.common.listview.ListItemAdapter;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHGroup;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

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
        PHBridge phBridge = HueManager.getPHBridge();
//        Log.d("MyApp", "GroupsFragment.updateData: mAdapter:" + (mAdapter != null) + " bridge:" + (phBridge != null));
        if (mAdapter != null && phBridge != null) {
            List<PHGroup> groups = phBridge.getResourceCache().getAllGroups();

            List<ListItem> data = new ArrayList<>();
            for (PHGroup group : groups) {
                String identifier = group.getIdentifier();
                data.add(new GroupItem(identifier));
            }

            mAdapter.clear();
            mAdapter.addAll(data);
        }
    }

    private class GroupItem extends ListItem {
        private String mIdentifier;

        private String mName;
        private int mState;

        public GroupItem(String identifier) {
            mIdentifier = identifier;
            mState = -1;
        }

        private PHBridge getBridge() {
            return HueManager.getPHBridge();
        }

        private PHGroup getGroup() {
            return getBridge().getResourceCache().getGroups().get(mIdentifier);
        }

        @Override
        public void initView(View v) {
        }

        @Override
        public void updateView(View v) {
            PHGroup group = getGroup();

            mName = group.getName();

            TextView itemName = (TextView) v.findViewById(R.id.item_name);
            itemName.setText(mName);

            TextView itemState = (TextView) v.findViewById(R.id.item_state);
            ImageView image = (ImageView) v.findViewById(R.id.item_icon);

            mState = getGroupState(group);

            if (mState < 0) {   // disabled
                itemState.setText("Disconnected (All)");
                image.setImageDrawable(v.getResources().getDrawable(R.drawable.ic_light_disabled));
                image.setColorFilter(v.getResources().getColor(R.color.colorDisable));
            } else if (mState > 0) {   // on
                image.setImageDrawable(v.getResources().getDrawable(R.drawable.ic_light_on));
                int color = v.getResources().getColor(R.color.colorOn);
                image.setColorFilter(color);
                if (mState > 1) {
                    itemState.setText("On (All)");
                } else {
                    itemState.setText("On (Some)");
                }
            } else {        // off
                itemState.setText("Off (All)");
                image.setImageDrawable(v.getResources().getDrawable(R.drawable.ic_light_off));
                image.setColorFilter(v.getResources().getColor(R.color.colorOff));
            }
        }

        private int getGroupState(PHGroup group) {
            PHBridge bridge = getBridge();
            boolean anyReachable = false;
            boolean anyOn = false;
            boolean allOn = true;

            for (String identifier : group.getLightIdentifiers()) {
                PHLight light = bridge.getResourceCache().getLights().get(identifier);
                PHLightState lightState = light.getLastKnownLightState();
                if (lightState.isReachable()) {
                    anyReachable = true;
                    if (lightState.isOn()) {
                        anyOn = true;
                    } else {
                        allOn = false;
                    }
                } else {
                    allOn = false;
                }
            }

            return anyReachable ? allOn ? 2 : anyOn ? 1 : 0 : -1;
        }

        private void setOn(boolean on) {
            PHBridge bridge = getBridge();
            PHGroup group = getGroup();

            for (String identifier : group.getLightIdentifiers()) {
                PHLight phLight = bridge.getResourceCache().getLights().get(identifier);

                PHLightState state = phLight.getLastKnownLightState();
                state.setOn(on);
                state.setTransitionTime(0);

                getBridge().updateLightState(phLight, state);
                phLight.setLastKnownLightState(state);
            }

            mState = (on ? 1 : 0);
        }

        @Override
        public void onClick(View v) {
            if (mState >= 0) {
                setOn(mState == 0);
                updateView(v);
            } else {
                String msg = "All lights in " + mName + " are disconnected";
                Toast.makeText(v.getContext(), msg, Toast.LENGTH_SHORT).show();
            }

        }
    }

}
