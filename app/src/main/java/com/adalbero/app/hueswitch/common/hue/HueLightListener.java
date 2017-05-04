package com.adalbero.app.hueswitch.common.hue;

import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;

import java.util.List;
import java.util.Map;

/**
 * Created by Adalbero on 08/04/2017.
 */

public class HueLightListener implements PHLightListener {
    @Override
    public void onReceivingLightDetails(PHLight phLight) {

    }

    @Override
    public void onReceivingLights(List<PHBridgeResource> list) {

    }

    @Override
    public void onSearchComplete() {

    }

    @Override
    public void onSuccess() {

    }

    @Override
    public void onError(int i, String s) {

    }

    @Override
    public void onStateUpdate(Map<String, String> map, List<PHHueError> list) {

    }
}
