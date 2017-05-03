package com.adalbero.app.hueswtich;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.adalbero.app.hueswtich.common.settings.SettingsActivity;
import com.adalbero.app.hueswtich.controller.AppController;
import com.adalbero.app.hueswtich.view.BulbsFragment;
import com.adalbero.app.hueswtich.view.GroupsFragment;
import com.adalbero.app.hueswtich.view.HomeFragment;

public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private HomeFragment mHomeFragment;
    private BulbsFragment mBulbsFragment;
    private GroupsFragment mGroupsFragment;

    private AppController mAppController;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

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

        mAppController = AppController.getInstance(this);
//        mAppController.hueConnect();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAppController.setContext(this);
        mAppController.hueConnect();
        updateView();
    }

    public void updateView() {
        TextView v = (TextView)findViewById(R.id.main_status);
        if (mAppController.hueIsBridgeOffLine(false)) {
            v.setVisibility(View.VISIBLE);
            v.setText("Bridge is offline");
        } else if (!mAppController.hueIsBridgeConnected()){
            v.setVisibility(View.VISIBLE);
            v.setText("No bridge connected");
        } else {
            v.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                if (mAppController.hueIsBridgeConnected()) {
                    startActivity(new Intent(this, SettingsActivity.class));
                } else {
                    mAppController.hueConnect();
                }
                return true;
            case R.id.menu_reset:
                reset();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void reset() {
        mAppController.reset();

        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mAppController.destroy();
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
