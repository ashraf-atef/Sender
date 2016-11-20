package com.example.ashraf.sender.PreviousActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;

import com.example.ashraf.sender.Main.MainActivity;
import com.example.ashraf.sender.R;

/**
 * Created by ashraf on 11/20/2016.
 */

public class PreviousMainActivityPresenterImpl implements PreviousMainActivityPresenter {

    PreviousMainActivity previousMainActivity ;
    public PreviousMainActivityPresenterImpl(PreviousMainActivity previousMainActivity) {
        this.previousMainActivity=previousMainActivity ;
    }

    @Override
    public void checkGPS() {
        LocationManager lm = (LocationManager) previousMainActivity.getBaseContext().getSystemService(previousMainActivity.getBaseContext().LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled ) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(new ContextThemeWrapper(previousMainActivity, R.style.myDialog));
            dialog.setMessage(previousMainActivity.getBaseContext().getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(previousMainActivity.getBaseContext().getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    previousMainActivity.startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton(previousMainActivity.getBaseContext().getString(R.string.cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    previousMainActivity.finish();
                }
            });
            dialog.show();
        }
        else
        {
            redirect();
        }
    }

    @Override
    public void redirect() {
        new Thread()
        {
            @Override
            public void run() {
                super.run();
                try {
                    sleep(2000);
                    previousMainActivity.startActivity(new Intent(previousMainActivity,MainActivity.class).setFlags((Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
