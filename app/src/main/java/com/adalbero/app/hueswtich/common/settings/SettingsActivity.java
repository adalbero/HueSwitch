package com.adalbero.app.hueswtich.common.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.adalbero.app.hueswtich.R;
import com.adalbero.app.hueswtich.common.hue.HueManager;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLight;

import java.util.List;

/**
 * Created by Adalbero on 05/04/2017.
 */

public class SettingsActivity extends AppCompatActivity {
    public static final String PREF_BULB_COLOR = "pref_bulb_color";
    public static final String PREF_BULB_BRI = "pref_bulb_bri";
    public static final String PREF_KEY_FAVORITE = "pref_key_favorite";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setTitle("Settings");
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
        }

        private void initFavoriteBulb() {
            ListPreference bulbList = (ListPreference)findPreference(PREF_KEY_FAVORITE);
            PHBridge bridge = HueManager.getPHBridge();
            if (bridge != null) {
                List<PHLight> lights = bridge.getResourceCache().getAllLights();
                String[] entries = new String[lights.size()];
                String[] entryValues = new String[lights.size()];

                for (int i=0; i<lights.size(); i++) {
                    entries[i] = lights.get(i).getName();
                    entryValues[i] = lights.get(i).getIdentifier();
                }

                bulbList.setEntries(entries);
                bulbList.setEntryValues(entryValues);
            }
        }

    }

    public static SharedPreferences getPreferences(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref;
    }

}
