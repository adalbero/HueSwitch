package com.adalbero.app.hueswtich;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adalbero.app.hueswtich.common.listview.ListItem;

/**
 * Created by Adalbero on 04/04/2017.
 */

public class HomeFragment extends Fragment {

    private View mView;

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

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        updateCache();
    }

    public void updateCache() {
        ListItem favorite = getFavorite();
        if (favorite != null) {
            favorite.updateView(mView);
        }
    }

    public void updateData() {
        updateCache();
    }

    public void onClickIcon() {
        ListItem favorite = getFavorite();
        if (favorite != null) {
            favorite.onClick(mView);
        }
    }

    public ListItem getFavorite() {
        ListItem favorite = null;

        MainActivity main = (MainActivity)getActivity();
        if (main != null) {
            favorite = main.getFavorite();
        }

        return favorite;
    }
}
