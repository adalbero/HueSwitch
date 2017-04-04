package com.adalbero.app.hueswtich.common.hue;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.adalbero.app.hueswtich.R;
import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHMessageType;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueParsingError;

import java.util.List;

/**
 * Created by Adalbero on 04/04/2017.
 */

public class HuePushlinkActivity extends Activity {
    private ProgressBar pbar;
    private static final int MAX_TIME=30;
    private PHHueSDK phHueSDK;
    private boolean isDialogShowing;

    private int CANCEL = -123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("MyApp", "HuePushlinkActivity.onCreate: ");

        setContentView(R.layout.activity_hue_pushlink);
        setTitle("Pushlink");

        isDialogShowing=false;
        phHueSDK = PHHueSDK.getInstance();

        pbar = (ProgressBar) findViewById(R.id.countdownPB);
        pbar.setMax(MAX_TIME);

        phHueSDK.getNotificationManager().registerSDKListener(listener);

        Button btnCancel = (Button)findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onError(CANCEL, "User canceled.");
            }
        });
    }

    @Override
    protected void onStop(){
        super.onStop();
        phHueSDK.getNotificationManager().unregisterSDKListener(listener);
    }

    public void incrementProgress() {
        pbar.incrementProgressBy(1);
    }

    private PHSDKListener listener = new PHSDKListener() {

        @Override
        public void onAccessPointsFound(List<PHAccessPoint> arg0) {}

        @Override
        public void onAuthenticationRequired(PHAccessPoint arg0) {}

        @Override
        public void onBridgeConnected(PHBridge bridge, String username) {}

        @Override
        public void onCacheUpdated(List<Integer> arg0, PHBridge bridge) {}

        @Override
        public void onConnectionLost(PHAccessPoint arg0) {}

        @Override
        public void onConnectionResumed(PHBridge arg0) {}

        @Override
        public void onError(final int code, final String message) {
            if (code == PHMessageType.PUSHLINK_BUTTON_NOT_PRESSED) {
                incrementProgress();
            }
            else if (code == PHMessageType.PUSHLINK_AUTHENTICATION_FAILED || code == CANCEL || code == 52) {
                incrementProgress();

                if (!isDialogShowing) {
                    isDialogShowing=true;
                    HuePushlinkActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            String msg = (code == CANCEL ? "Canceled by user" : "Authentication failed");
                            AlertDialog.Builder builder = new AlertDialog.Builder(HuePushlinkActivity.this);
                            builder.setMessage(msg).setNeutralButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            finish();
                                        }
                                    });

                            builder.create();
                            builder.show();
                        }
                    });
                }

            }


        } // End of On Error

        @Override
        public void onParsingErrors(List<PHHueParsingError> parsingErrorsList) {}
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (listener !=null) {
            phHueSDK.getNotificationManager().unregisterSDKListener(listener);
        }
    }

}
