package com.adalbero.app.hueswitch.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.adalbero.app.hueswitch.R;
import com.adalbero.app.hueswitch.common.listview.ListItem;
import com.adalbero.app.hueswitch.controller.AppController;
import com.adalbero.app.hueswitch.controller.AppListener;

/**
 * Created by Adalbero on 04/04/2017.
 */

public class HomeFragment extends Fragment implements AppListener {

    private View mView;

    private AppController mAppController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home, container, false);

        mView = v.findViewById(R.id.item_view);

        View iconView = v.findViewById(R.id.item_icon);
        iconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickIcon();
            }
        });

        mAppController = AppController.getInstance();
        mAppController.registerAppListener(this);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        onDataChanged(true);
    }

    public void onClickIcon() {
        ListItem favorite = mAppController.getFavorite();
        if (favorite != null) {
            favorite.onClick(mView);
        } else if (!mAppController.hueIsBridgeConnected()) {
            Toast.makeText(getActivity(), "Click on Settings to connect", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Select a Bulb/Group on Settings", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDataChanged(boolean bInit) {
        ListItem favorite = mAppController.getFavorite();
        if (favorite != null) {
            favorite.updateView(mView);
        } else {
//            TextView nameView = (TextView) mView.findViewById(R.id.item_name);
//            if (mAppController.hueIsBridgeConnected()) {
//                nameView.setText("No Bulb/Group");
//            } else {
//                nameView.setText("No bridge connected");
//            }
        }
    }

}
