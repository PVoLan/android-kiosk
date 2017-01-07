package ru.pvolan.testkiosk;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    TextView textNotADeviceAdmin;
    TextView textNoPermission;
    TextView textKioskModeWorking;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);

        textNotADeviceAdmin = (TextView) findViewById (R.id.text_not_a_device_admin);
        textNoPermission = (TextView) findViewById (R.id.text_no_permission);
        textKioskModeWorking = (TextView) findViewById (R.id.text_kiosk_mode_working);

        findViewById (R.id.button_exit_kiosk_mode).setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                stopLock ();
            }
        });

        requestKeepScreenOn();
    }


    @Override
    protected void onResume () {
        super.onResume ();
        requestTaskLock();
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            requestImmersiveMode();
        }
    }


    //****************************************
    //Tools

    private void requestKeepScreenOn() {
        //Keep screen highlight always on. This is not required for kiosk mode, but makes things more nice
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


    private void requestImmersiveMode() {
        //Enable full-screen mode. This is not required for kiosk mode, but makes things more nice
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }


    private void requestTaskLock() {
            DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName deviceAdminReceiver = new ComponentName(this, MyDeviceAdminReceiver.class);

            if (dpm.isDeviceOwnerApp(this.getPackageName()))
            {
                String[] packages = {this.getPackageName()};
                dpm.setLockTaskPackages(deviceAdminReceiver, packages);

                if (dpm.isLockTaskPermitted(this.getPackageName())) {
                    startLockTask(); //Working in full mode
                    showThisViewAndHideOthers (textKioskModeWorking);
                } else {
                    startLockTask(); //Working in emulation mode
                    showThisViewAndHideOthers (textNoPermission);
                }

            } else {
                startLockTask(); //Working in emulation mode
                showThisViewAndHideOthers (textNotADeviceAdmin);
            }
    }


    private void stopLock(){

        //Note. Double call for stopLockTask() sometimes causes crash, so you should check if you app is really in lock mode
        if(isAppInLockTaskMode()) {
            stopLockTask();
            showThisViewAndHideOthers (null);
        } else {
            Toast.makeText (this, R.string.not_in_kiosk_mode, Toast.LENGTH_SHORT).show ();
        }
    }


    @Override
    public void finish () {
        //Note. When your app is in lock mode, you can just call finish() from code and activity will be closed as usually
        //But I do not recommend to do so - I've got some unstable behaviour in this case. This is my personal experience and can be wrong
        //I recommend always call stopLockTask() explicitly

        if(isAppInLockTaskMode ()) return;
        super.finish ();
    }


    private boolean isAppInLockTaskMode() {
        ActivityManager activityManager;

        activityManager = (ActivityManager)
                this.getSystemService(Context.ACTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // For SDK version 23 and above.
            return activityManager.getLockTaskModeState() != ActivityManager.LOCK_TASK_MODE_NONE;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // When SDK version >= 21. This API is deprecated in 23.
            return activityManager.isInLockTaskMode();
        }

        return false;
    }


    private void showThisViewAndHideOthers (View v){
        if(v == textNotADeviceAdmin) textNotADeviceAdmin.setVisibility (View.VISIBLE);
        else textNotADeviceAdmin.setVisibility (View.GONE);

        if(v == textNoPermission) textNoPermission.setVisibility (View.VISIBLE);
        else textNoPermission.setVisibility (View.GONE);

        if(v == textKioskModeWorking) textKioskModeWorking.setVisibility (View.VISIBLE);
        else textKioskModeWorking.setVisibility (View.GONE);
    }

}
