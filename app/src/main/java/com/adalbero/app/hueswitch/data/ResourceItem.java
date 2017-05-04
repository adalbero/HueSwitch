package com.adalbero.app.hueswitch.data;

import com.adalbero.app.hueswitch.common.listview.ListItem;

/**
 * Created by Adalbero on 06/04/2017.
 */

public class ResourceItem extends ListItem {
    protected String mIdentifier;

    public ResourceItem(String identifier) {
        mIdentifier = identifier;
    }

    public String getIdentifier() {
        return mIdentifier;
    }

}
