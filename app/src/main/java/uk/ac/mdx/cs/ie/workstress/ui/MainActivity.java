/*Copyright 2016 WorkStress Experiment
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package uk.ac.mdx.cs.ie.workstress.ui;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadFactory;

import uk.ac.mdx.cs.ie.workstress.R;
import uk.ac.mdx.cs.ie.workstress.service.IStressService;
import uk.ac.mdx.cs.ie.workstress.service.StressService;
import uk.ac.mdx.cs.ie.workstress.utility.DialogReturnInterface;
import uk.ac.mdx.cs.ie.workstress.utility.ExplicitIntentGenerator;
import uk.ac.mdx.cs.ie.workstress.utility.StressReport;

/**
 * Main application Activity
 *
 * @author Dean Kramer <d.kramer@mdx.ac.uk>
 */
public class MainActivity extends AppCompatActivity implements DialogReturnInterface {

    private IStressService mStressService;
    private MainActivityFragment mMainFragment;
    private Context mContext;
    private boolean mBound = false;
    private static final String STRESS_PREFS = "StressPrefs";
    private static final String USER_PREF = "userid";
    private static final String REPORT_PREF = "reportid";
    private static final String REPORT_SUBMIT_TIME_PREF = "reportstime";
    private static final String USER_NAME = "username";
    private String mUser;
    public String mUsername = "";
    public boolean mIsPaired = true;
    private SharedPreferences mSettings;
    private FragmentManager mFragManager;
    private FloatingActionButton mFabButton;
    private static final String LOG_TAG = "WorkStressActivity";
    public boolean mReportNeeded = false;
    private int mReportNumber = 0;
    private boolean mNoDoze = true;
    private boolean mJustStarted = false;
    private BroadcastReceiver mBReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        bindToService();

        mSettings = mContext.getSharedPreferences(STRESS_PREFS, 0);

        String device = mSettings.getString("macaddress", "");

        if (device.isEmpty()) {
            mIsPaired = false;
        }

        mReportNumber = mSettings.getInt(REPORT_PREF, 0);


        if (mReportNumber > 0) {
            long current = System.currentTimeMillis() / 1000L;

            int reportDue = mSettings.getInt(REPORT_SUBMIT_TIME_PREF, Integer.MAX_VALUE);

            if (reportDue != Integer.MAX_VALUE) {
                reportDue += 3600;
            }

            if (reportDue > current) {
                mReportNeeded = true;
            }
        }

        setupUI();

        mJustStarted = true;

    }

    private void switchToReport(boolean toReport) {

        if (true) {
            mReportNeeded = true;
            mReportNumber = mSettings.getInt(REPORT_PREF, 0);
            mFabButton.setVisibility(View.VISIBLE);
        } else {
            mReportNeeded = true;
            mReportNumber = mSettings.getInt(REPORT_PREF, 0);
            mFabButton.setVisibility(View.INVISIBLE);
        }

        if (mStressService != null) {
            try {
                mStressService.dismissNotification();
            } catch (RemoteException e) {
                Log.e(LOG_TAG, e.getMessage().toString());
            }
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(mContext, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);

            }
        });
    }

    private void setupUI() {
        setContentView(R.layout.activity_main);

        mFragManager = getSupportFragmentManager();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mFabButton = (FloatingActionButton) findViewById(R.id.fab);

        if (mReportNeeded) {
            toolbar.setTitle(R.string.newreport);
            mFabButton.setVisibility(View.VISIBLE);
        }

        setSupportActionBar(toolbar);


        if (mFabButton != null) {
            mFabButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (mBound) {

                        if (mUser.equals("")) {
                            Snackbar.make(view, getText(R.string.userunknown), Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        } else {
                            final StressReport data = mMainFragment.getStressData();


                            final Timer timer = new Timer();

                            Snackbar snackbar = Snackbar.make(view, getText(R.string.reportsubmitted), Snackbar.LENGTH_LONG)
                                    .setAction(R.string.undo, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            timer.cancel();
                                        }
                                    });

                            snackbar.show();

                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    try {
                                        mStressService.sendReport(data);

                                        switchToReport(false);
                                    } catch (RemoteException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, snackbar.getDuration() + 500);
                        }

                    } else {
                        Snackbar.make(view, getText(R.string.serviceNotBound), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }

                }
            });
        }

        mMainFragment = (MainActivityFragment) mFragManager.findFragmentById(R.id.mainfragment);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PowerManager powerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
            String packageName = mContext.getPackageName();

            if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
                mNoDoze = false;
                Snackbar.make(mFabButton, getText(R.string.batteryignore), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }

        }

    }

    private void bindToService() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent serviceIntent = new Intent(IStressService.class.getName());

                serviceIntent = ExplicitIntentGenerator
                        .createExplicitFromImplicitIntent(mContext, serviceIntent);

                if (serviceIntent == null) {
                    Snackbar.make(mFabButton, getText(R.string.servicecantbind), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
                }
            }
        }).start();
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mStressService = IStressService.Stub.asInterface(service);
            mBound = !mBound;

            if (!mUsername.isEmpty()) {
                startMonitor();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mStressService = null;
            mBound = !mBound;
        }
    };

    public void unBindFromService() {
        if (mBound) {
            unbindService(mConnection);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unBindFromService();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mBReceiver != null) {
            unregisterReceiver(mBReceiver);
        }

    }

    public void logout(View view) {

        try {
            if (mStressService.isCollecting()) {
                mStressService.stopHeartMonitor();
            }
        } catch (RemoteException e) {
            Log.e(LOG_TAG, e.getMessage());
        }

        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(USER_PREF, "");
        editor.putString(USER_NAME, "");
        editor.commit();

        finish();
    }

    public void pairBluetoothDevice(View v) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Intent i = new Intent(this, BluetoothDeviceActivity.class);
            startActivity(i);
        }
    }

    private void setUsername() {
        TextDialogFragment userFragment = TextDialogFragment.newInstance(R.string.password);
        userFragment.show(mFragManager, "userdialog");
    }

    private void startMonitor() {

        if (!mNoDoze) {
            Snackbar.make(mFabButton, getText(R.string.needbatteryignore), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

        try {

            if (!mStressService.isCollecting()) {
                if (mUser.equals("")) {
                    Snackbar.make(mFabButton, getText(R.string.userunknown), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    mStressService.startHeartMonitor();
                }
            }

        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    @Override
    public void doPositiveButtonClick(Object... para) {
        String password = (String) para[0];

        if (password.equals("setusername")) {
            //if (password.equals("")) {
            Intent intent = new Intent(this, UserSelectionActivity.class);
            startActivity(intent);
        } else {
            Snackbar.make(mFabButton, getText(R.string.incorrectpaw), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

            TextDialogFragment userFragment = TextDialogFragment.newInstance(R.string.password);
            userFragment.show(mFragManager, "userdialog");
        }
    }

    @Override
    public void doNegativeButtonClick(Object... para) {
        finish();
    }

    @Override
    public void doNeutralButtonClick(Object... para) {

    }

    @Override
    public void onBackPressed() {

        try {
            if (mBound) {
                if (mStressService.isCollecting()) {
                    Snackbar.make(mFabButton, getText(R.string.cannotback), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    super.onBackPressed();
                }
            } else {
                super.onBackPressed();
            }
        } catch (RemoteException e) {
            Log.e(LOG_TAG, e.getMessage().toString());
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        mUser = mSettings.getString(USER_PREF, "");
        mUsername = mSettings.getString(USER_NAME, "");

        if (mUsername.isEmpty()) {
            setUsername();
        } else {
            mReportNumber = mSettings.getInt(REPORT_PREF, 0);

            if ((mReportNumber > 0) && (mReportNeeded)) {
                if (mJustStarted) {
                    mJustStarted = false;
                } else {
                    switchToReport(true);
                }
            }

            mBReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    boolean needed = intent.getExtras().getBoolean(StressService.BROADCAST_NEEDED, false);

                    if (needed != mReportNeeded) {
                        switchToReport(needed);
                    }
                }
            };

            IntentFilter filter = new IntentFilter(StressService.BROADCAST_INTENT);
            registerReceiver(mBReceiver, filter);
        }
    }

}
