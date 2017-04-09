package com.adalbero.app.hueswtich.data;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adalbero.app.hueswtich.R;
import com.adalbero.app.hueswtich.common.hue.HueManager;
import com.adalbero.app.hueswtich.common.settings.SettingsActivity;
import com.adalbero.app.hueswtich.controller.AppController;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

/**
 * Created by Adalbero on 06/04/2017.
 */

public class BulbItem extends ResourceItem {

    private AppController mAppController;

    public BulbItem(String identifier) {
        super(identifier);
        mAppController = AppController.getInstance();
    }

    public PHLight getLight() {
        return HueManager.getPHBridge().getResourceCache().getLights().get(mIdentifier);
    }

    public String getName() {
        return getLight().getName();
    }

    public PHLightState getLightState() {
        return getLight().getLastKnownLightState();
    }

    public int getState() {
        PHLightState lightState = getLightState();
        return lightState.isReachable() ? lightState.isOn() ? 1 : 0 : -1;
    }

    @Override
    public void initView(View v) {
    }

    @Override
    public void updateView(View v) {
        SharedPreferences settings = mAppController.getPreferences();

        TextView itemName = (TextView) v.findViewById(R.id.item_name);
        TextView itemState = (TextView) v.findViewById(R.id.item_state);
        ImageView image = (ImageView) v.findViewById(R.id.item_icon);

        String name = getName();
        itemName.setText(name);

        int state = getState();

        if (state < 0) {   // disabled
            image.setImageDrawable(v.getResources().getDrawable(R.drawable.ic_light_disabled));
            image.setColorFilter(v.getResources().getColor(R.color.colorDisable));
            itemState.setText("Disconnected");
        } else if (state == 1) {   // on
            image.setImageDrawable(v.getResources().getDrawable(R.drawable.ic_light_on));

            PHLightState lightState = getLightState();
            if (settings.getBoolean(SettingsActivity.PREF_BULB_COLOR, false)) {
                float[] hsv = getLightColor(lightState);
                image.setColorFilter(Color.HSVToColor(hsv));
            } else {
                image.setColorFilter(v.getResources().getColor(R.color.colorOn));
            }
            String text = "On";
            if (settings.getBoolean(SettingsActivity.PREF_BULB_BRI, false)) {
                text = text + " (" + formatBri(lightState.getBrightness()) + ")";
            }
            itemState.setText(text);
        } else {        // off
            itemState.setText("Off");
            image.setImageDrawable(v.getResources().getDrawable(R.drawable.ic_light_off));
            image.setColorFilter(v.getResources().getColor(R.color.colorOff));
        }
    }

    private float[] getLightColor(PHLightState lightState) {

        final float MIN_BRI = 0.4f;

        float b = lightState.getBrightness() / 254f * (1- MIN_BRI) + MIN_BRI;
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

    private String formatBri(int bri) {
        if (bri <= 1) {
            return "min";
        } else if (bri >= 254) {
            return "max";
        } else {
            return String.format("%d%%", bri * 100 / 254 + 1);
        }
    }

    @Override
    public void onClick(View v) {
        if (mAppController.hueIsBridgeOffLine(true)) {
            return;
        }

        int state = getState();
        if (state >= 0) {
            PHLight light = getLight();
            HueManager.setOn(light, state == 0);
        } else {
            String msg = getName() + " is disconnected";
            Toast.makeText(v.getContext(), msg, Toast.LENGTH_SHORT).show();
        }

    }

}

