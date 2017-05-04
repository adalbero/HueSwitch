package com.adalbero.app.hueswitch.common.hue;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.WindowManager;

/**
 * Created by Adalbero on 04/04/2017.
 */

public final class HueAlertDialog {

    private ProgressDialog pdialog;
    private static HueAlertDialog dialogs;

    private HueAlertDialog() {

    }

    public static synchronized HueAlertDialog getInstance() {
        if (dialogs == null) {
            dialogs = new HueAlertDialog();
        }
        return dialogs;
    }

    public static void showErrorDialog(Context activityContext, String msg, int btnNameResId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activityContext);
        builder.setTitle("Error").setMessage(msg).setPositiveButton(btnNameResId, null);
        AlertDialog alert = builder.create();
        alert.getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        if (! ((Activity) activityContext).isFinishing()) {
            alert.show();
        }

    }

    public void closeProgressDialog() {

        if (pdialog != null) {
            pdialog.dismiss();
            pdialog = null;
        }
    }

    public void showProgressDialog(String message, Context ctx) {
        pdialog = ProgressDialog.show(ctx, null, message, true, true);
        pdialog.setCancelable(false);

    }

}
