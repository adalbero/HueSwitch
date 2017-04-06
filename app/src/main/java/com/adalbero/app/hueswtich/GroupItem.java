package com.adalbero.app.hueswtich;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adalbero.app.hueswtich.common.hue.HueManager;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHGroup;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

/**
 * Created by Adalbero on 06/04/2017.
 */

class GroupItem extends ResourceItem {

    public GroupItem(String identifier) {
        super(identifier);
    }

    private PHGroup getGroup() {
        return HueManager.getPHBridge().getResourceCache().getGroups().get(mIdentifier);
    }

    public String getName() {
        return getGroup().getName();
    }

    public int getState() {
        PHBridge bridge = HueManager.getPHBridge();
        PHGroup group = getGroup();

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


    @Override
    public void initView(View v) {
    }

    @Override
    public void updateView(View v) {
        TextView itemName = (TextView) v.findViewById(R.id.item_name);
        TextView itemState = (TextView) v.findViewById(R.id.item_state);
        ImageView image = (ImageView) v.findViewById(R.id.item_icon);

        String name = getName();
        itemName.setText(name);


        int state = getState();

        if (state < 0) {   // disabled
            itemState.setText("Disconnected (All)");
            image.setImageDrawable(v.getResources().getDrawable(R.drawable.ic_light_disabled));
            image.setColorFilter(v.getResources().getColor(R.color.colorDisable));
        } else if (state > 0) {   // on
            image.setImageDrawable(v.getResources().getDrawable(R.drawable.ic_light_on));
            int color = v.getResources().getColor(R.color.colorOn);
            image.setColorFilter(color);
            if (state > 1) {
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


    @Override
    public void onClick(View v) {
        int state = getState();
        if (state >= 0) {
            PHGroup group = getGroup();
            HueManager.setOn(group, state == 0);
            updateView(v);
        } else {
            String msg = "All lights in " + getName() + " are disconnected";
            Toast.makeText(v.getContext(), msg, Toast.LENGTH_SHORT).show();
        }

    }
}

