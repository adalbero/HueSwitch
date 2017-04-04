package com.adalbero.app.hueswtich;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.adalbero.app.hueswtich.common.hue.HueManager;
import com.philips.lighting.hue.sdk.PHMessageType;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private HueManager mHueManager;

    private HomeFragment mHomeFragment;
    private BulbsFragment mBulbsFragment;
    private GroupsFragment mGroupsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mHomeFragment = new HomeFragment();
        mBulbsFragment = new BulbsFragment();
        mGroupsFragment = new GroupsFragment();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(mViewPager);

        mHueManager = new HueManager(this) {
            @Override
            public void onConnect() {
                super.onConnect();
                updateData();
            }

            @Override
            public void onUpdateCache(final List<Integer> list) {
                super.onUpdateCache(list);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateCache(list);
                    }
                });
            }
        };

        if (mHueManager.tryToConnect(true)) {
            updateData();
        }
    }

    private void updateCache(List<Integer> list) {
        Log.d("MyApp", "MainActivity.updateCache: ");
        if (list.contains(PHMessageType.LIGHTS_CACHE_UPDATED)) {
            Log.d("MyApp", "MainActivity.updateCache: Light");
            mBulbsFragment.updateCache();
            mGroupsFragment.updateCache();
        }

        if (list.contains(PHMessageType.GROUPS_CACHE_UPDATED)) {
            Log.d("MyApp", "MainActivity.updateCache: Group");
            mGroupsFragment.updateCache();
        }
    }

    private void updateData() {
        mBulbsFragment.updateData();
        mGroupsFragment.updateData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHueManager.finalize();
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final int PAGE_HOME = 0;
        private final int PAGE_BULBS = 1;
        private final int PAGE_GROUPS = 2;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case PAGE_HOME:
                default:
                    return mHomeFragment;
                case PAGE_BULBS:
                    return mBulbsFragment;
                case PAGE_GROUPS:
                    return mGroupsFragment;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case PAGE_HOME:
                default:
                    return "Favorite";
                case PAGE_BULBS:
                    return "Bulbs";
                case PAGE_GROUPS:
                    return "Groups";
            }
        }
    }

}
