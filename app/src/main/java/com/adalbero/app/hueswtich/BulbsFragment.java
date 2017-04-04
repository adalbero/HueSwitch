package com.adalbero.app.hueswtich;

import android.graphics.Color;
import android.os.Bundle;
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
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

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
        return v;
    }

    public void updateCache() {
        mAdapter.notifyDataSetChanged();
    }

    public void updateData(HueManager hueManager) {
        PHBridge phBridge = hueManager.getPHBridge();
        List<PHLight> phLights = phBridge.getResourceCache().getAllLights();

        List<ListItem> data = new ArrayList<>();
        for (PHLight phLight : phLights) {
            String identifier = phLight.getIdentifier();
            data.add(new BulbItem(identifier));
        }

        mAdapter.clear();
        mAdapter.addAll(data);
    }

    private class BulbItem extends ListItem {
        private String mIdentifier;

        private String mName;
        private int mState;

        public BulbItem(String identifier) {
            mIdentifier = identifier;
            mState = -1;
        }

        private PHBridge getBridge() {
            PHHueSDK phHueSDK = PHHueSDK.getInstance();
            return phHueSDK.getSelectedBridge();
        }

        private PHLight getLight() {
            return getBridge().getResourceCache().getLights().get(mIdentifier);
        }

        @Override
        public void initView(View v) {
        }

        @Override
        public void updateView(View v) {
            PHLight light = getLight();

            mName = light.getName();

            PHLightState lightState = light.getLastKnownLightState();
            mState = lightState.isReachable() ? lightState.isOn() ? 1 : 0 : -1;

            TextView itemName = (TextView) v.findViewById(R.id.item_name);
            itemName.setText(mName);

            TextView itemState = (TextView) v.findViewById(R.id.item_state);
            ImageView image = (ImageView) v.findViewById(R.id.item_icon);
            if (mState < 0) {   // disabled
                itemState.setText("Disconnected");
                image.setImageDrawable(v.getResources().getDrawable(R.drawable.ic_light_disabled));
                image.setColorFilter(v.getResources().getColor(R.color.colorDisable));
            } else if (mState == 1) {   // on
                image.setImageDrawable(v.getResources().getDrawable(R.drawable.ic_light_on));
//                int color = v.getResources().getColor(R.color.colorOn);
                float[] hsv = getLightColor(lightState);
                int color = Color.HSVToColor(hsv);
                image.setColorFilter(color);
                itemState.setText(String.format("On (%.0f%%)", hsv[2]*100));
            } else {        // off
                itemState.setText("Off");
                image.setImageDrawable(v.getResources().getDrawable(R.drawable.ic_light_off));
                image.setColorFilter(v.getResources().getColor(R.color.colorOff));
            }
        }

        private float[] getLightColor(PHLightState lightState) {
            float min = 0.4f;
            float b = lightState.getBrightness() / 254f * (1-min) + min;
            float h;
            float s;

            try {
                h = lightState.getHue() / 65535f * 360f;
                s = lightState.getSaturation() / 254f;
            } catch (Exception ex) {
                h = 0f;
                s = 0f;
            }

            return new float[]{h, s, b};
        }

        private void setOn(boolean on) {
            PHLight phLight = getLight();

            PHLightState state = phLight.getLastKnownLightState();
            state.setOn(on);
            state.setTransitionTime(0);

            getBridge().updateLightState(phLight, state);
            phLight.setLastKnownLightState(state);
        }

        @Override
        public void onClick(View v) {
            if (mState >= 0) {
                setOn(mState == 0);
                updateView(v);
            } else {
                String msg = mName + " is disconnected";
                Toast.makeText(v.getContext(), msg, Toast.LENGTH_SHORT).show();
            }

        }
    }

}
