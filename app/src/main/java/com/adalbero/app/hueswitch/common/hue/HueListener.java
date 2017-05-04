package com.adalbero.app.hueswitch.common.hue;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueParsingError;

import java.util.List;

/**
 * Created by Adalbero on 04/04/2017.
 */

public class HueListener implements PHSDKListener {
    @Override
    public void onCacheUpdated(List<Integer> list, PHBridge phBridge) {

    }

    @Override
    public void onBridgeConnected(PHBridge phBridge, String s) {

    }

    @Override
    public void onAuthenticationRequired(PHAccessPoint phAccessPoint) {

    }

    @Override
    public void onAccessPointsFound(List<PHAccessPoint> list) {

    }

    @Override
    public void onError(int i, String s) {

    }

    @Override
    public void onConnectionResumed(PHBridge phBridge) {

    }

    @Override
    public void onConnectionLost(PHAccessPoint phAccessPoint) {

    }

    @Override
    public void onParsingErrors(List<PHHueParsingError> list) {

    }
}
