package com.example.greyson.test1.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import com.example.greyson.test1.R;
import com.example.greyson.test1.ui.base.BaseActivity;
import com.example.greyson.test1.widget.CirclePercentView;

import java.text.SimpleDateFormat;

/**
 * Tis class is about tracker function
 *
 * @author Greyson, Carson
 * @version 1.0
 */
public class TrackerActivity extends BaseActivity {
    //private static final String TAG = TrackerActivity.class.getSimpleName();
    //public static final int MSG_UNCOLOR_START = 0;
    //public static final int MSG_UNCOLOR_STOP = 1;
    //public static final int MSG_COLOR_START = 2;
    //public static final int MSG_COLOR_STOP = 3;
    //public static final int MSG_SERVICE_OBJ = 4;
    //public static final String MESSENGER_INTENT_KEY  = BuildConfig.APPLICATION_ID + ".MESSENGER_INTENT_KEY";
    //public static final String WORK_DURATION_KEY = BuildConfig.APPLICATION_ID + ".WORK_DURATION_KEY";

    private Intent setting;
    private String cusTime;
    public MediaPlayer mp;
    private Handler mHandler;
    private Runnable dTimer;
    private Runnable wTimer;
    private Runnable sTimer;
    private CirclePercentView mCirclePercentView;
    private Button staButton;
    private Button canButton;
    private WebView webView;
    private String id;
    private String lat;
    private String lng;
    private String c1;
    private String name;
    private String num;

    //private ComponentName mServiceComponent;
    //private JobSchedulerService mjobSchedulerService;

    /**
     * This method is to connect to layout
     *
     * @return
     */
    @Override
    protected int getLayoutRes() {
        return R.layout.frag_trackeractivity;
    }

    /**
     * This method is to initial view
     *
     * @return
     */
    @Override
    protected void initView() {

        setting = getIntent();
        mHandler = new Handler();

        canButton = (Button) findViewById(R.id.canButton);
        staButton = (Button) findViewById(R.id.staButton);
        webView = (WebView) findViewById(R.id.webView);


        //  This method is to start ringing
        try {
            mp.setDataSource(this, RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_RINGTONE));
            mp.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mp = new MediaPlayer();
        wTimer = new Runnable() {
            @Override
            public void run() {
                warningDialog();

            }
        };
        dTimer = new Runnable() {
            @Override
            public void run() {
                dialog();
                mp.start();
                mHandler.postDelayed(wTimer, 65000);
            }
        };

        staButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startUpload();
                mHandler.postDelayed(dTimer, countTime());
                initialProcessBar();
            }
        });

        canButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.removeCallbacks(dTimer);
                finishUpload();
                finish();
            }
        });
    }

    @Override
    protected void initData() {
        Bundle b = setting.getExtras();
        id = b.getString("id");
       cusTime = b.getString("not").toString().trim();
        c1 = b.getString("con");
        lat = b.getString("lat");
        lng = b.getString("lng");
        num = b.getString("num");
        name = b.getString("nam");
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void destroyView() {

    }

    /**
     * This method is to receive time
     */
    private int countTime() {
        return Integer.valueOf(cusTime) * 60000;

    }
    private void initialProcessBar() {
        setContentView(R.layout.frag_trackeractivity);
        mCirclePercentView = (CirclePercentView) findViewById(R.id.circleView);
        int t = 1000*countTime()/100000;
        mCirclePercentView.setPercent(100, t);
    }

    private void dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you safe?\nYou have 1min to confirm");
        builder.setTitle("Alarm");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mp.stop();
                uploadData();
                dialog.dismiss();
                mHandler.removeCallbacks(wTimer);
                mHandler.postDelayed(dTimer, countTime());
            }
        });
        builder.create().show();
    }

    private void warningDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("We have sent warning messages, please contact your friends");
        builder.setTitle("Alarm");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void uploadData() {

    }

    private void startUpload() {

        SimpleDateFormat format =   new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        Long time=new Long(445555555);
        String d = format.format(time);
        webView.loadUrl("http://usafe2.epnjkefarc.us-west-2.elasticbeanstalk.com/trailtrack/create/?deviceid=" + id + d + "&c1=+61452585390&c2=+61452585390&c3=+61435634716&status=start&period=" + cusTime + "&lat=" + lat + " &lng=-144");
    }

    private void finishUpload() {

    }

    /**
     private void scheduleJob(View v) {
     JobInfo.Builder builder = new JobInfo.Builder(1, mServiceComponent);
     builder.setMinimumLatency(1000);
     JobScheduler tm = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
     tm.schedule(builder.build());
     mServiceComponent = new ComponentName(this, JobSchedulerService.class);
     mhandlre = new Handler() {
    @Override public void handleMessage(Message msg) {
    switch (msg.what) {
    case MSG_UNCOLOR_START:
    dialog();
    break;
    case MSG_UNCOLOR_STOP:
    dialog();
    break;
    case MSG_SERVICE_OBJ:
    mjobSchedulerService = (JobSchedulerService) msg.obj;
    mjobSchedulerService.setUiCallback(TrackerActivity.this);
    break;
    }
    }

    };
     }

     public void cancelAllJobs(View v) {
     JobScheduler tm = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
     tm.cancelAll();
     Toast.makeText(TrackerActivity.this, "Canceled", Toast.LENGTH_SHORT).show();
     }

     public void onReceivedStartJob(JobParameters params) {
     Message m = Message.obtain(mhandlre, MSG_UNCOLOR_START);
     mhandlre.sendMessageDelayed(m, 1000L); // uncolour in 1 second.
     }

     public void onReceivedStopJob() {
     Message m = Message.obtain(mhandlre, MSG_UNCOLOR_STOP);
     mhandlre.sendMessageDelayed(m, 2000L); // uncolour in 1 second.
     }
     */
}

