package com.adalbero.app.hueswtich.common.settings;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;

import com.adalbero.app.hueswtich.R;
import com.adalbero.app.hueswtich.common.hue.HueManager;
import com.adalbero.app.hueswtich.controller.AppController;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHGroup;
import com.philips.lighting.model.PHLight;

import java.util.List;

/**
 * Created by Adalbero on 05/04/2017.
 */

public class SettingsActivity extends AppCompatActivity {
    public static final String PREF_BULB_COLOR = "pref_bulb_color";
    public static final String PREF_BULB_BRI = "pref_bulb_bri";
    public static final String PREF_KEY_FAVORITE = "pref_key_favorite";

    private AppController mAppControler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setTitle("Settings");

        mAppControler = AppController.getInstance(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAppControler.setContext(this);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);

        getFragmentManager().beginTransaction().replace(R.id.fragment, new SettingsFragment()).commit();
    }

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            initFavoriteBulb();

            Preference pref1 = findPreference("version");
            pref1.setSummary(appVersion());
        }

        private void initFavoriteBulb() {
            ListPreference bulbList = (ListPreference) findPreference(PREF_KEY_FAVORITE);
            PHBridge bridge = HueManager.getPHBridge();
            if (bridge != null) {
                List<PHLight> lights = bridge.getResourceCache().getAllLights();
                List<PHGroup> groups = bridge.getResourceCache().getAllGroups();

                int n = lights.size() + groups.size();
                int i = 0;

                String[] entries = new String[n];
                String[] entryValues = new String[n];

                for (PHLight light : lights) {
                    entries[i] = "Bulb: " + light.getName();
                    entryValues[i] = "B:" + light.getIdentifier();
                    i++;
                }

                for (PHGroup group : groups) {
                    entries[i] = "Group: " + group.getName();
                    entryValues[i] = "G:" + group.getIdentifier();
                    i++;
                }

                bulbList.setEntries(entries);
                bulbList.setEntryValues(entryValues);
            }
        }

        public String appVersion() {
            try {
                PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
                return pInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                return "unknown";
            }
        }

    }

}
