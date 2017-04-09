package com.adalbero.app.hueswtich.controller;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.adalbero.app.hueswtich.common.hue.HueManager;
import com.adalbero.app.hueswtich.common.listview.ListItem;
import com.adalbero.app.hueswtich.common.settings.SettingsActivity;
import com.adalbero.app.hueswtich.data.BulbItem;
import com.adalbero.app.hueswtich.data.GroupItem;
import com.adalbero.app.hueswtich.data.ResourceItem;
import com.philips.lighting.hue.sdk.PHMessageType;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHGroup;
import com.philips.lighting.model.PHLight;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Adalbero on 08/04/2017.
 */

public class AppController {
    private static AppController instance;

    private Activity mContext;

    private List<ResourceItem> mResources = new ArrayList<>();
    private HueManager mHueManager;
    private Set<AppListener> mAppListeners = new HashSet<>();


    private AppController(Activity context) {
        mContext = context;
        initHueManager();
    }

    public static AppController getInstance(Activity context) {
        if (instance == null) {
            instance = new AppController(context);
        }

        return instance;
    }

    public static AppController getInstance() {
        return getInstance(null);
    }

    public void registerAppListener(AppListener listener) {
        mAppListeners.add(listener);
    }

    private void initHueManager() {
        mHueManager = new HueManager(mContext) {
            @Override
            public void onConnect() {
                super.onConnect();
                updateData();
            }

            @Override
            public void onUpdateCache(final List<Integer> list) {
                super.onUpdateCache(list);
                updateCache(list);
            }
        };
    }

    private void updateData() {
        PHBridge phBridge = HueManager.getPHBridge();
        mResources.clear();

        List<PHLight> phLights = phBridge.getResourceCache().getAllLights();
        for (PHLight phLight : phLights) {
            mResources.add(new BulbItem(phLight.getIdentifier()));
        }

        List<PHGroup> phGroups = phBridge.getResourceCache().getAllGroups();
        for (PHGroup phGroup : phGroups) {
            mResources.add(new GroupItem(phGroup.getIdentifier()));
        }

        notifyDataChanged(true);
    }

    private void updateCache(List<Integer> list) {
        if (list.contains(PHMessageType.LIGHTS_CACHE_UPDATED)) {
            notifyDataChanged(false);
        }

        if (list.contains(PHMessageType.GROUPS_CACHE_UPDATED)) {
            notifyDataChanged(false);
        }
    }

    public void notifyDataChanged(final boolean bInit) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (AppListener listener : mAppListeners) {
                    listener.onDataChanged(bInit);
                }
            }
        });
    }

    public void setContext(Activity context) {
        mContext = context;
        mHueManager.setContext(context);
    }

    public void hueConnect() {
        if (mHueManager.tryToConnect(true)) {
            updateData();
        }
    }

    public boolean hueIsBridgeConnected() {
        return mHueManager.getStatus() > 0;
    }

    public boolean hueIsBridgeOffLine(boolean bShowToast) {
        boolean isOffline = mHueManager.getStatus() == HueManager.OFF_LINE;

        if (isOffline && bShowToast) {
            Toast.makeText(getCurrentActivity(), "Bridge is offline", Toast.LENGTH_SHORT).show();
        }
        return isOffline;

    }

    public Activity getCurrentActivity() {
        return mContext;
    }

    public void reset() {
        SharedPreferences.Editor edit = getPreferences().edit();
        edit.clear();
        edit.commit();

        mHueManager.destroy();
        mResources.clear();
    }

    public void destroy() {
        mHueManager.destroy();
    }

    public List<ResourceItem> getData() {
        return mResources;
    }

    public ListItem getFavorite() {
        String value = getPreferences().getString(SettingsActivity.PREF_KEY_FAVORITE, null);
        if (value != null) {
            String params[] = value.split(":");
            String type = params.length > 1 ? params[0] : "B";
            String identifier = params.length > 1? params[1] : params[0];

            for (ResourceItem item : mResources) {
                if (item.getIdentifier().equals(identifier)) {
                    if (type.equals("B") && item instanceof BulbItem) {
                        return item;
                    } else if (type.equals("G") && item instanceof GroupItem) {
                        return item;
                    }
                }
            }
        }

        return null;
    }

    public SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(mContext);
    }
}
