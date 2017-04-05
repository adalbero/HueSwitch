package com.adalbero.app.hueswtich;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adalbero.app.hueswtich.common.hue.HueManager;
import com.adalbero.app.hueswtich.common.settings.SettingsActivity;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

/**
 * Created by Adalbero on 04/04/2017.
 */

public class HomeFragment extends Fragment {

    private String mIdentifier = "2";
    private String mName;
    private int mState;

    private TextView mNameView;
    private TextView mStateView;
    private ImageView mIconView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home, container, false);

        mNameView = (TextView)v.findViewById(R.id.item_name);
        mStateView = (TextView)v.findViewById(R.id.item_state);
        mIconView = (ImageView)v.findViewById(R.id.item_icon);

        mIconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickIcon();
            }
        });
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        mIdentifier = SettingsActivity.getPreferences(getActivity()).getString(SettingsActivity.PREF_KEY_FAVORITE, "1");

        updateView();
    }

    public void updateCache() {
        updateView();
    }

    public void updateData() {
        updateView();
    }

    private PHLight getLight() {
        PHBridge bridge = HueManager.getPHBridge();
        if (bridge == null) return null;

        return bridge.getResourceCache().getLights().get(mIdentifier);
    }

    public void updateView() {
        PHLight light = getLight();
        if (light == null) return;
        if (mNameView == null) return;

        mName = light.getName();

        mNameView.setText(mName);

        PHLightState lightState = light.getLastKnownLightState();
        mState = lightState.isReachable() ? lightState.isOn() ? 1 : 0 : -1;

        if (mState < 0) {   // disabled
            mStateView.setText("Disconnected");
            mIconView.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_light_disabled));
            mIconView.setColorFilter(this.getResources().getColor(R.color.colorDisable));
        } else if (mState == 1) {   // on
            mStateView.setText("On");
            mIconView.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_light_on));
            mIconView.setColorFilter(this.getResources().getColor(R.color.colorOn));

        } else {        // off
            mStateView.setText("Off");
            mIconView.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_light_off));
            mIconView.setColorFilter(this.getResources().getColor(R.color.colorOff));
        }

    }

    private void setOn(boolean on) {
        PHLight phLight = getLight();
        PHBridge bridge = HueManager.getPHBridge();

        PHLightState state = phLight.getLastKnownLightState();
        state.setOn(on);
        state.setTransitionTime(0);

        bridge.updateLightState(phLight, state);
        phLight.setLastKnownLightState(state);
    }


    public void onClickIcon() {
        if (mState >= 0) {
            setOn(mState == 0);
            updateView();
        } else {
            String msg = mName + " is disconnected";
            Toast.makeText(this.getContext(), msg, Toast.LENGTH_SHORT).show();
        }

    }


}
