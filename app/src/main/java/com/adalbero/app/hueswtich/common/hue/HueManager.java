package com.adalbero.app.hueswtich.common.hue;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.adalbero.app.hueswtich.R;
import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHMessageType;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHGroup;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHHueParsingError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

import java.util.List;

/**
 * Created by Adalbero on 04/04/2017.
 */

public class HueManager {
    private static String TAG = "MyApp";

    private Activity mContext;

    private PHHueSDK phHueSDK;
    private boolean lastSearchWasIPScan = false;

    public HueManager(Activity context) {
        mContext = context;

        // Gets an instance of the Hue SDK.
        phHueSDK = PHHueSDK.create();

        // Set the Device Name (name of your app). This will be stored in your bridge whitelist entry.
        phHueSDK.setAppName("HueSwitch");
        phHueSDK.setDeviceName(android.os.Build.MODEL);

        // Register the PHSDKListener to receive callbacks from the bridge.
        phHueSDK.getNotificationManager().registerSDKListener(listener);
    }

    public boolean tryToConnect(boolean fBridgeSearch) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String lastIpAddress = prefs.getString("lastIpAddress", "");
        String lastUsername = prefs.getString("lastUsername", "");

        // Automatically try to connect to the last connected IP Address.  For multiple bridge support a different implementation is required.
        if (lastIpAddress != null && !lastIpAddress.equals("")) {
            PHAccessPoint lastAccessPoint = new PHAccessPoint();
            lastAccessPoint.setIpAddress(lastIpAddress);
            lastAccessPoint.setUsername(lastUsername);

            if (phHueSDK.isAccessPointConnected(lastAccessPoint)) {
                return true;
            } else {
                HueAlertDialog.getInstance().showProgressDialog("Connecting last AccessPoint...", mContext);
                phHueSDK.connect(lastAccessPoint);
            }

        } else if (fBridgeSearch) {  // First time use, so perform a bridge search.
            doBridgeSearch();
        }

        return false;
    }

    public void doBridgeSearch() {
        HueAlertDialog.getInstance().showProgressDialog("Searching for Bridge ...", mContext);
        PHBridgeSearchManager sm = (PHBridgeSearchManager) phHueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE);
        // Start the UPNP Searching of local bridges.
        sm.search(true, true);
    }

    public void onConnect() {
        phHueSDK.enableHeartbeat(phHueSDK.getSelectedBridge(), PHHueSDK.HB_INTERVAL);
    }

    public void onUpdateCache(List<Integer> list) {
    }

    public void connect(PHAccessPoint accessPoint) {
//        Log.d("MyApp", "HueManager.connect: ");

        PHBridge connectedBridge = phHueSDK.getSelectedBridge();

        if (connectedBridge != null) {
            String connectedIP = connectedBridge.getResourceCache().getBridgeConfiguration().getIpAddress();
            if (connectedIP != null) {   // We are already connected here:-
                phHueSDK.disableHeartbeat(connectedBridge);
                phHueSDK.disconnect(connectedBridge);
            }
        }

        HueAlertDialog.getInstance().showProgressDialog("Connecting to Bridge ...", mContext);
        phHueSDK.connect(accessPoint);
    }

    public void finalize() {
        phHueSDK.getNotificationManager().unregisterSDKListener(listener);
//        phHueSDK.disableAllHeartbeat();
    }

    public static PHBridge getPHBridge() {
        return PHHueSDK.getInstance().getSelectedBridge();
    }

    public static void setOn(PHLight light, boolean on) {
        PHLightState state = new PHLightState();

        state.setOn(on);

        HueManager.getPHBridge().updateLightState(light, state);
    }

    public static void setOn(PHGroup group, boolean on) {
        PHBridge bridge = HueManager.getPHBridge();

        for (String identifier : group.getLightIdentifiers()) {
            PHLight phLight = bridge.getResourceCache().getLights().get(identifier);
            setOn(phLight, on);
        }
    }

    public void disconnect() {
        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(mContext).edit();

        prefs.remove("lastIpAddress");
        prefs.remove("lastUsername");

        prefs.apply();
    }

    private HueListener listener = new HueListener() {
        @Override
        public void onCacheUpdated(List<Integer> list, PHBridge phBridge) {
            onUpdateCache(list);
        }

        @Override
        public void onBridgeConnected(PHBridge bridge, String username) {

            phHueSDK.setSelectedBridge(bridge);
            phHueSDK.enableHeartbeat(bridge, PHHueSDK.HB_INTERVAL);
            phHueSDK.getLastHeartbeat().put(bridge.getResourceCache().getBridgeConfiguration().getIpAddress(), System.currentTimeMillis());

            SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(mContext).edit();

            prefs.putString("lastIpAddress", bridge.getResourceCache().getBridgeConfiguration().getIpAddress());
            prefs.putString("lastUsername", username);
            prefs.apply();

            HueAlertDialog.getInstance().closeProgressDialog();

            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onConnect();
                }
            });

        }

        @Override
        public void onAuthenticationRequired(PHAccessPoint accessPoint) {
            phHueSDK.startPushlinkAuthentication(accessPoint);
            HueAlertDialog.getInstance().closeProgressDialog();
            mContext.startActivity(new Intent(mContext, HuePushlinkActivity.class));
        }

        @Override
        public void onAccessPointsFound(List<PHAccessPoint> accessPoint) {

            HueAlertDialog.getInstance().closeProgressDialog();
            if (accessPoint != null && accessPoint.size() > 0) {
                phHueSDK.getAccessPointsFound().clear();
                phHueSDK.getAccessPointsFound().addAll(accessPoint);

                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        connect(phHueSDK.getAccessPointsFound().get(0));
                    }
                });
            }

        }

        @Override
        public void onConnectionResumed(PHBridge bridge) {
            if (mContext.isFinishing())
                return;

            phHueSDK.getLastHeartbeat().put(bridge.getResourceCache().getBridgeConfiguration().getIpAddress(), System.currentTimeMillis());
            for (int i = 0; i < phHueSDK.getDisconnectedAccessPoint().size(); i++) {

                if (phHueSDK.getDisconnectedAccessPoint().get(i).getIpAddress().equals(bridge.getResourceCache().getBridgeConfiguration().getIpAddress())) {
                    phHueSDK.getDisconnectedAccessPoint().remove(i);
                }
            }

        }

        @Override
        public void onConnectionLost(PHAccessPoint accessPoint) {

            if (!phHueSDK.getDisconnectedAccessPoint().contains(accessPoint)) {
                phHueSDK.getDisconnectedAccessPoint().add(accessPoint);
            }

        }

        @Override
        public void onError(int code, final String message) {

            if (code == PHHueError.NO_CONNECTION) {
                Log.w(TAG, "On No Connection");
            } else if (code == PHHueError.AUTHENTICATION_FAILED || code == PHMessageType.PUSHLINK_AUTHENTICATION_FAILED) {
                Log.w(TAG, "Authentication Failed.");
                HueAlertDialog.getInstance().closeProgressDialog();
            } else if (code == PHHueError.BRIDGE_NOT_RESPONDING) {
                Log.w(TAG, "Bridge Not Responding . . . ");
                HueAlertDialog.getInstance().closeProgressDialog();
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        HueAlertDialog.showErrorDialog(mContext, message, R.string.btn_ok);
                    }
                });

            } else if (code == PHMessageType.BRIDGE_NOT_FOUND) {
                Log.w(TAG, "Bridge Not Found . . . ");

                if (!lastSearchWasIPScan) {  // Perform an IP Scan (backup mechanism) if UPNP and Portal Search fails.
                    Log.w(TAG, "Perfoming an IP Scan . . . ");

                    phHueSDK = PHHueSDK.getInstance();
                    PHBridgeSearchManager sm = (PHBridgeSearchManager) phHueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE);
                    sm.search(false, false, true);
                    lastSearchWasIPScan = true;
                } else {
                    HueAlertDialog.getInstance().closeProgressDialog();
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            HueAlertDialog.showErrorDialog(mContext, message, R.string.btn_ok);
                        }
                    });
                }

            } else if (code == 45) {
                HueAlertDialog.getInstance().closeProgressDialog();
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        HueAlertDialog.showErrorDialog(mContext, message, R.string.btn_ok);
                    }
                });
            }

        }

        @Override
        public void onParsingErrors(List<PHHueParsingError> errorList) {
            for (PHHueParsingError parsingError : errorList) {
                Log.e(TAG, "ParsingError : " + parsingError.getMessage());
            }

        }
    };

}
