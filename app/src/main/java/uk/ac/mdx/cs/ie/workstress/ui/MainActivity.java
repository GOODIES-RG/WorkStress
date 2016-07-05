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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

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
    private static final String USER_PREF = "username";
    private static final String REPORT_PREF = "reportid";
    private String mUser;
    private SharedPreferences mSettings;
    private FragmentManager mFragManager;
    private FloatingActionButton mFabButton;
    private static final String LOG_TAG = "WorkStressActivity";
    public boolean mReportNeeded = false;
    private int mReportNumber = 0;
    private Menu mMenu;
    private boolean mNoDoze = true;
    private Toolbar mToolbar;
    private boolean mJustStarted = false;
    private BroadcastReceiver mBReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        bindToService();

        mSettings = mContext.getSharedPreferences(STRESS_PREFS, 0);
        mUser = mSettings.getString(USER_PREF, "");

        mReportNumber = mSettings.getInt(REPORT_PREF, 0);

        if (mReportNumber > 0) {
            mReportNeeded = true;
        }

        setupUI();

        mJustStarted = true;

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

        try {
            mStressService.dismissNotification();
        } catch (RemoteException e) {
            Log.e(LOG_TAG, e.getMessage().toString());
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
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mFabButton = (FloatingActionButton) findViewById(R.id.fab);

        if (mReportNeeded) {
            mToolbar.setTitle(R.string.newreport);
            mFabButton.setVisibility(View.VISIBLE);
        }

        setSupportActionBar(mToolbar);


        if (mFabButton != null) {
            mFabButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (mBound) {

                        if (mUser == "") {
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
        unregisterReceiver(mBReceiver);
        unBindFromService();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        mMenu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_setname:
                setUsername();
                break;

            case R.id.action_startstop:
                startStopMonitor();
                break;

            case R.id.pair_device:
                pairBluetoothDevice();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void pairBluetoothDevice() {
        Intent i = new Intent(this, BluetoothDeviceActivity.class);
        startActivity(i);
    }

    private void setUsername() {
        TextDialogFragment userFragment = TextDialogFragment.newInstance(R.string.action_setname);
        userFragment.setText(mUser);
        userFragment.show(mFragManager, "userdialog");

    }

    private void startStopMonitor() {

        if (mNoDoze) {
            try {
                MenuItem changeUsername = mMenu.getItem(0);

                if (mStressService.isCollecting()) {
                    mStressService.stopHeartMonitor();
                    changeUsername.setEnabled(true);
                } else {
                    if (mUser.isEmpty()) {
                        Snackbar.make(mFabButton, getText(R.string.userunknown), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } else {
                        mStressService.startHeartMonitor();
                        changeUsername.setEnabled(false);
                    }
                }

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            Snackbar.make(mFabButton, getText(R.string.needbatteryignore), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    @Override
    public void doPositiveButtonClick(Object... para) {

        mUser = (String) para[0];
        mUser = mUser.toLowerCase();
        try {
            mStressService.setUsername(mUser);
        } catch (RemoteException e) {
            Log.e(LOG_TAG, "Cannot Set Username");
        }
    }

    @Override
    public void doNegativeButtonClick(Object... para) {
    }

    @Override
    public void doNeutralButtonClick(Object... para) {

    }

    @Override
    public void onBackPressed() {

        try {
            if (mStressService.isCollecting()) {
                Snackbar.make(mFabButton, getText(R.string.cannotback), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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

        mReportNumber = mSettings.getInt(REPORT_PREF, 0);

        if ((mReportNumber > 0) && (mReportNeeded)) {
            if (mJustStarted) {
                mJustStarted = false;
            } else {
                switchToReport(true);
            }
        }
    }

}
