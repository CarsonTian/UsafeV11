package com.example.greyson.test1.ui.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.greyson.test1.R;
import com.example.greyson.test1.ui.activity.TrackerActivity;
import com.example.greyson.test1.ui.base.BaseFragment;

import static android.content.Context.MODE_PRIVATE;


/**
 * Tis class is about tracker function
 * @author Greyson, Carson
 * @version 1.0
 */
public class SafetyTrackFragment extends BaseFragment{

    private Button okButton;
    private EditText cusTime;
    private EditText nameText;
    private static final int REQUEST_GET_DEVICEID = 222;
    private String id;
    private String number;
    private String laContact;
    private String cLatitude;
    private String cLngtitude;





    /**
     * This method is used to initialize the map view and request the current location
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_safetytrack, container, false);

        okButton = (Button) view.findViewById(R.id.okButton);
        cusTime = (EditText) view.findViewById(R.id.cusTime);
        nameText = (EditText) view.findViewById(R.id.nameText);

        // This is ok button function
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (checkNotTime() && checkName()) {
                   String notice = cusTime.getText().toString().trim();
                   String name = nameText.getText().toString().trim();
                   passTravelSetting(notice, name);
               }

            }
        });

        //checkNameDialog();
        checkDeviceIDPermission();
        return view;
    }

    @Override
    protected void initData() {
        getCurrentLocation();
        getContactList();
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void destroyView() {

    }

    /**
    protected void getDuration() {
        Map<String, String> params = new HashMap<>();
        //String ori = "";
        String ori = getCurrentLocation();
        String des = String.valueOf(desInput.getText().toString());
        params.put("origin", ori);
        params.put("destination", des);
        params.put("mode", "walking");
        params.put("key", "AIzaSyAYPtaZmfpFdvNd3_-ur4X2Bvn-35uVoAQ");

        mRetrofit3.create(WSNetService3.class)
                .getDuration(params)
                .subscribeOn(Schedulers.io())
                .compose(this.<TrackerRes>bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<TrackerRes>() {

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(TrackerRes tracker) {
                        try {
                                for (TrackerRes.RoutesBean rRes : tracker.getRoutes()) {
                                    for (TrackerRes.RoutesBean.LegsBean lRes : rRes.getLegs()) {
                                        TrackerRes.RoutesBean.LegsBean.DurationBean dRes = lRes.getDuration();
                                        box.setText(dRes.getText().toString().toString());
                                    }
                                }
                            } catch (Exception e) {}
                    }
                });
    }
    */

    private void getCurrentLocation() {
        SharedPreferences preferences1 = mContext.getSharedPreferences("LastLocation",MODE_PRIVATE);
        String[] array = preferences1.getString("last location","0,0").split(",");
        cLatitude = array[0];
        cLngtitude = array[1];
    }

    private void getContactList() {
        SharedPreferences preferences = mContext.getSharedPreferences("LastContact", MODE_PRIVATE);
        String lastContact = preferences.getString("contact", null);
        if (lastContact == null) {
            Toast.makeText(mContext, "Emergency Contact is Empty.", Toast.LENGTH_LONG).show();
            return;
        } else {
            laContact = lastContact;
        }
    }

    private boolean checkNotTime() {
        String notice = cusTime.getText().toString().trim();
        if (notice.equals("")) {
            Toast.makeText(mContext,"please fill out notice time",Toast.LENGTH_SHORT).show();
            return false;
        } else if (Integer.valueOf(notice) <= 5) {
            Toast.makeText(mContext,"please make sure time is longer than 5 min",Toast.LENGTH_SHORT).show();
            return false;
        } else if (Integer.valueOf(notice) >= 30) {
            Toast.makeText(mContext, "please make sure time is shorter than 30 min", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean checkName() {
        String name = nameText.getText().toString().trim();
        if (name.equals("")) {
            Toast.makeText(mContext, "Please input your name", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private void checkDeviceIDPermission() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_PHONE_STATE)) {

            } else {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_GET_DEVICEID);
            }
        } else {
            getMobileIMEI();
        }
    }

    private void checkNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setTitle("Tips");
        builder.setView(inflater.inflate(R.layout.frag_inputname, null));
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Later", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    /**
     * request permission
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_GET_DEVICEID:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getMobileIMEI();}
            }break;
        }
    }

    private void getMobileIMEI() {
        TelephonyManager tManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
         id = tManager.getDeviceId() ;
        number = tManager.getLine1Number();
    }

    /**
     *
     * @param tim
     */
    private void passTravelSetting(String tim, String name) {
        Intent intent = new Intent();
        intent.setClass(mContext, TrackerActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("num", number);
        intent.putExtra("not", tim);
        intent.putExtra("con", laContact);
        intent.putExtra("lat", cLatitude);
        intent.putExtra("lng", cLngtitude);
        intent.putExtra("nam", name);
        startActivityForResult(intent,1);
    }
}
